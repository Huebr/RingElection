import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.*;
import java.util.*;

/**
 * Created by pedro-jorge on 27/07/2016.
 */
public class Process implements Runnable{
    private int pid;
    private int priority;
    private int port;
    private int coodenador;
    private int x;
    private int rc_flag;
    ArrayList<Integer> activeList;
    Map<Integer,Integer> vizinhos;

    /*Process(int pid,int priority){
        setPid(pid);
        setPriority(priority);
    }*/

    public void setPid(int pid) {
        this.pid = pid;
        setPriority(pid);
    }

    private void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPid() {
        return pid;
    }

    public int getPriority() {
        return priority;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    private void register() throws IOException {
        Socket client = new Socket("127.0.0.1",6346); //Connection MasterApp
        Scanner s = new Scanner(client.getInputStream());
        while(!client.isClosed()){
            if(s.hasNextInt()) {
                setPid(s.nextInt());
                setPort(s.nextInt());
                client.close();
            }
        }

    }
    private  void  updateListener() {
        try {
            ServerSocket serverListener = new ServerSocket(port);
            while(true){
                try {
                    Message msg=null;
                    Socket client = serverListener.accept();
                    while (!client.isClosed()) {//tem que ajeitar
                        ObjectInputStream bufferInput = new ObjectInputStream(client.getInputStream());
                        msg = (Message) bufferInput.readObject();
                        client.close();
                    }
                    if (msg != null) {
                        switch(msg.getType()){
                            case 0: //System.out.println("Tabela de Vizinhos de "+getPort()+" : \n");
                                    vizinhos = (Map<Integer,Integer>) msg.getContent();
                                    /*for(Integer viz:vizinhos.keySet()){
                                       if(viz!=getPort()){
                                            System.out.println("Conhece Vizinho "+viz+" Porta "+vizinhos.get(viz));
                                        }
                                     }*/
                                     break;
                            case 1:
                                        ArrayList<Integer> active = (ArrayList<Integer>) msg.getContent();

                                        try {
                                            int next = get_nextnode();
                                            if (active.contains(getPid())) {
                                                Socket nclient = new Socket("127.0.0.1", vizinhos.get(next));
                                                ObjectOutputStream bufferStream = new ObjectOutputStream(nclient.getOutputStream());
                                                bufferStream.flush();
                                                Message nmsg = new Message(2, Collections.max(activeList));
                                                bufferStream.writeObject(nmsg);
                                                bufferStream.close();
                                                nclient.close();
                                            } else {
                                                active.add(getPid());
                                                activeList= new ArrayList<>(active);
                                                System.out.println(activeList);
                                                Socket nclient = new Socket("127.0.0.1", vizinhos.get(next));
                                                ObjectOutputStream bufferStream = new ObjectOutputStream(nclient.getOutputStream());
                                                bufferStream.flush();
                                                Message nmsg = new Message(1, activeList);
                                                bufferStream.writeObject(nmsg);
                                                bufferStream.close();
                                                nclient.close();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    break;
                            case 2:
                                    Integer ncoordenator = (Integer)msg.getContent();
                                    if(getCoodenador() != ncoordenator){
                                        setCoodenador(ncoordenator);
                                        System.out.println(getPid()+" "+getCoodenador());
                                        int next= 0;
                                        try {
                                            next = get_nextnode();
                                            Socket nclient = new Socket("127.0.0.1", vizinhos.get(next));
                                            ObjectOutputStream bufferStream = new ObjectOutputStream(nclient.getOutputStream());
                                            bufferStream.flush();
                                            Message nmsg = new Message(2,getCoodenador());
                                            bufferStream.writeObject(nmsg);
                                            bufferStream.close();
                                            nclient.close();
                                            rc_flag=0;
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;
                            case 3:break;
                            case 4:
                                //System.out.println(msg.getContent());
                                break;
                            default: System.out.println("Messagem Invalida");
                                     break;
                        }
                    }
                }catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int try_connect(int p) {
        try {
            Socket client = new Socket();
            int time = 1000;
            client.connect(new InetSocketAddress("127.0.0.1", vizinhos.get(p)), time);
            //System.out.println("\nConectado com sucesso com vizinho");
            ObjectOutputStream bufferStream = new ObjectOutputStream(client.getOutputStream());
            bufferStream.flush();
            Message msg = new Message(4, "hello");
            bufferStream.writeObject(msg);
            return 0;
        }catch (IOException e){
            return -1;
        }
    }
    private int get_nextnode() {
        int flag=0;
        for(Integer p:vizinhos.keySet()){
            if(flag==1){
                if(try_connect(p)!=-1)return p;
            }
            if(p==getPid()){
                flag=1;
            }
        }
        for(Integer p:vizinhos.keySet()){
            if(p==getPid()){
                break;
            }
            if(try_connect(p)!=-1)return p;
        }
        return -1;
    }
    public void election() {
        try {
            while(true) {
                if (vizinhos.isEmpty()) {
                    Thread.sleep(5000);
                }
                else if(getPid()!=getCoodenador()){
                    try {
                        Socket client = new Socket();
                        int time = 1000;
                        if(vizinhos.get(getCoodenador())==null)throw new IOException();
                        client.connect(new InetSocketAddress("127.0.0.1",vizinhos.get(getCoodenador())),time);
                        System.out.println("\nConectado com sucesso com coordenador");
                        ObjectOutputStream bufferStream = new ObjectOutputStream(client.getOutputStream());
                        bufferStream.flush();
                        Message msg = new Message(4,"hello");
                        bufferStream.writeObject(msg);
                        Thread.sleep(10000);

                    } catch (IOException e) {
                        System.out.println(getPid()+" cannot connect "+ getCoodenador() );
                        try{
                            if(rc_flag==0) {
                                rc_flag=1;
                                int next_node = get_nextnode();
                                Socket client = new Socket("127.0.0.1", vizinhos.get(next_node));
                                //System.out.println("Sending Update to " + id);
                                ObjectOutputStream bufferStream = new ObjectOutputStream(client.getOutputStream());
                                bufferStream.flush();
                                activeList = new ArrayList<>();
                                activeList.add(getPid());
                                Message msg = new Message(1, activeList);
                                bufferStream.writeObject(msg);
                                bufferStream.close();
                                client.close();
                            }
                        } catch (IOException f) {
                            f.printStackTrace();
                        }
                        Thread.sleep(10000);
                    }

                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        setCoodenador(1);//First process Coordenador
        rc_flag=0;
        vizinhos = new HashMap<Integer,Integer>();
        try {
            register();
            new Thread(this::updateListener).start();
            new Thread(this::election).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Started Process with id : "+getPid()+" and priority "+getPriority());
        /*Scanner sc = new Scanner(System.in);
        String cmd;
        cmd = sc.nextLine();
        while(!cmd.equals("exit")){//forma de desligar maybe
        }*/
    }

    public int getCoodenador() {
        return coodenador;
    }

    public void setCoodenador(int coodenador) {
        this.coodenador = coodenador;
    }
}
