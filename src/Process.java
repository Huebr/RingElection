import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * Created by pedro-jorge on 27/07/2016.
 */
public class Process implements Runnable{
    private int pid;
    private int priority;
    private int port;
    private int coordenador;
    volatile private int rc_flag;
    private int numero_de_conexoes;
    volatile private long start;
    volatile private boolean closed;
    volatile private boolean result;
    public int time = 10000;
    private ServerSocket serverListener;
    ArrayList<Integer> activeList;
    Map<Integer,IP> vizinhos;

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
        Socket client = new Socket(); //Connection MasterApp
        client.connect(new InetSocketAddress("10.0.0.1",6346),time);
        numero_de_conexoes++;
        Scanner s = new Scanner(client.getInputStream());
        client.setReuseAddress(true);
        while(!client.isClosed()){
            if(s.hasNextInt()) {
                setPid(s.nextInt());
                setPort(s.nextInt());
                client.close();
            }
        }

    }
    private  void  updateListener() {
        Logger logger = Logger.getLogger("App.update.log");
        logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
        try {
            serverListener = new ServerSocket(getPort());
            serverListener.setReuseAddress(true);
            while(!closed){
                try {
                    Message msg=null;
                    Socket client = serverListener.accept();
                    IP temp = new IP(client.getInetAddress().getHostAddress(),client.getPort());
                    while (!client.isClosed()) {//tem que ajeitar
                        ObjectInputStream bufferInput = new ObjectInputStream(client.getInputStream());
                        msg = (Message) bufferInput.readObject();
                        client.close();
                    }
                    if (msg != null) {
                        switch(msg.getType()){
                            case 0: //System.out.println("Tabela de Vizinhos de "+getPort()+" : \n");
                                    vizinhos = (Map<Integer,IP>) msg.getContent();
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
                                            if(next!=-1) {
                                                if (active.contains(getPid())) {
                                                    activeList = new ArrayList<>(active);
                                                    Socket nclient = new Socket();
                                                    nclient.connect(new InetSocketAddress(vizinhos.get(next).getIp(), vizinhos.get(next).getPort()),time);
                                                    logger.info(getPid() + " mandando mensagem com eleito para : " + next);
                                                    numero_de_conexoes++;
                                                    ObjectOutputStream bufferStream = new ObjectOutputStream(nclient.getOutputStream());
                                                    bufferStream.flush();
                                                    //System.out.println( activeList +" "+Collections.max(activeList));
                                                    Message nmsg = new Message(2, Collections.max(activeList));
                                                    bufferStream.writeObject(nmsg);
                                                    bufferStream.close();
                                                    nclient.close();
                                                } else {
                                                    active.add(getPid());
                                                    activeList = new ArrayList<>(active);
                                                    Socket nclient = new Socket();
                                                    nclient.connect(new InetSocketAddress(vizinhos.get(next).getIp(), vizinhos.get(next).getPort()),time);
                                                    logger.info(getPid() + " mandando mensagem com eleicao para : " + next);
                                                    numero_de_conexoes++;
                                                    ObjectOutputStream bufferStream = new ObjectOutputStream(nclient.getOutputStream());
                                                    bufferStream.flush();
                                                    Message nmsg = new Message(1, activeList);
                                                    bufferStream.writeObject(nmsg);
                                                    bufferStream.close();
                                                    nclient.close();
                                                }
                                            }
                                        } catch (IOException ignored) {
                                        }

                                    break;
                            case 2:
                                    Integer ncoordenator = (Integer)msg.getContent();
                                    if(getCoordenador() != ncoordenator){
                                        setCoordenador(ncoordenator);
                                        logger.info(getPid() + " configurou novo coordenador : " + getCoordenador());
                                        if(rc_flag==1)logger.info("tempo eleicao : " +(System.currentTimeMillis()-start)/1000+" s");
                                        int next= 0;
                                        try {
                                            next = get_nextnode();
                                            if(next!=-1) {
                                                Socket nclient = new Socket();
                                                nclient.connect(new InetSocketAddress(vizinhos.get(next).getIp(), vizinhos.get(next).getPort()),time);
                                                numero_de_conexoes++;
                                                ObjectOutputStream bufferStream = new ObjectOutputStream(nclient.getOutputStream());
                                                bufferStream.flush();
                                                Message nmsg = new Message(2, getCoordenador());
                                                bufferStream.writeObject(nmsg);
                                                bufferStream.close();
                                                nclient.close();
                                                rc_flag = 0;
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;
                            case 3:break;
                            case 4:
                                int id_conector = (int) msg.getContent();
                                if(id_conector==0){
                                    while(true) {
                                        try {
                                            logger.info("tentando registrar de novo.");
                                            register();
                                            break;
                                        } catch (IOException e) {

                                        }
                                    }
                                }
                                else if(!vizinhos.containsKey(id_conector)){
                                    try {
                                            Socket nclient = new Socket();
                                            nclient.connect(new InetSocketAddress(temp.getIp(),temp.getPort()),time);
                                            logger.warning("problema no processo : "+id_conector);
                                            numero_de_conexoes++;
                                            ObjectOutputStream bufferStream = new ObjectOutputStream(nclient.getOutputStream());
                                            bufferStream.flush();
                                            Message nmsg = new Message(4, 0);
                                            bufferStream.writeObject(nmsg);
                                            bufferStream.close();
                                            nclient.close();
                                    } catch (IOException e) {
                                    }
                                }
                                else logger.info(msg.getContent()+" conectou-se.");
                                break;
                            default: logger.warning("Messagem Invalida");
                                     break;
                        }
                    }
                }catch (IOException | ClassNotFoundException e){
                }
            }
        } catch (IOException e) {
        }
    }
    private int try_connect(int p) {
        try {
            Socket client = new Socket();
            client.connect(new InetSocketAddress(vizinhos.get(p).getIp(), vizinhos.get(p).getPort()), time);
            numero_de_conexoes++;
            //System.out.println("\nConectado com sucesso com vizinho");
            ObjectOutputStream bufferStream = new ObjectOutputStream(client.getOutputStream());
            bufferStream.flush();
            Message msg = new Message(3, "");
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
        Logger logger = Logger.getLogger("App.election.log");
        logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
        try {
            while(!closed) {
                result = getPid()!= getCoordenador();
                if (vizinhos.isEmpty()) {
                    Thread.sleep(5000);
                }
                else if(result){
                    try {
                        Socket client = new Socket();
                        if(vizinhos.get(getCoordenador())==null)throw new IOException();
                        else if(getCoordenador()<getPid())throw new IOException();
                        client.connect(new InetSocketAddress(vizinhos.get(getCoordenador()).getIp(), vizinhos.get(getCoordenador()).getPort()),time);
                        logger.info(getPid() + " Conectado com sucesso com coordenador " + getCoordenador());
                        numero_de_conexoes++;
                        ObjectOutputStream bufferStream = new ObjectOutputStream(client.getOutputStream());
                        bufferStream.flush();
                        Message msg = new Message(4,Integer.valueOf(getPid()));
                        bufferStream.writeObject(msg);
                        Thread.sleep(10000);

                    } catch (IOException e) {
                        logger.info(getPid() + " nao consegue conectar com " + getCoordenador());
                        try{
                            if(rc_flag==0) {
                                rc_flag=1;
                                start=System.currentTimeMillis();
                                int next_node = get_nextnode();
                                if(next_node!=-1) {
                                    Socket client = new Socket();
                                    client.connect(new InetSocketAddress(vizinhos.get(next_node).getIp(), vizinhos.get(next_node).getPort()),time);
                                    numero_de_conexoes++;
                                    logger.info(getPid() + " mandando mensagem de eleicao para :" + next_node);
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
        setCoordenador(1);//First process Coordenador
        numero_de_conexoes =0;
        closed=false;
        rc_flag=0;
        vizinhos = new HashMap<Integer,IP>();
        try {
            register();
            new Thread(this::updateListener).start();
            new Thread(this::election).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Started Process with id : " + getPid() + " and priority " + getPriority());
        try {
            TimeUnit.MINUTES.sleep(3);//3 minutes of processing
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        closed = true;
        try {
            serverListener.close();
            TimeUnit.SECONDS.sleep(3);
        } catch (IOException | InterruptedException ignored) {
        }
        System.out.println("Numero de conexoes efetuadas : "+ numero_de_conexoes);

    }

    public int getCoordenador() {
        return coordenador;
    }

    public void setCoordenador(int coordenador) {
        this.coordenador = coordenador;
    }
}
