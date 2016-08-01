import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by pedro-jorge on 27/07/2016.
 */
public class Process implements Runnable{
    private int pid;
    private int priority;
    private int port;
    private int coodenador;
    private int x;
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
        Socket client = new Socket("127.0.0.1",6345); //Connection MasterApp
        Scanner s = new Scanner(client.getInputStream());
        while(!client.isClosed()){
            if(s.hasNextInt()) {
                setPid(s.nextInt());
                setPort(s.nextInt());
                client.close();
            }
        }

    }
    private void updateListener(){
        try {
            ServerSocket serverListener = new ServerSocket(port);
            while(true){
                try {
                    Message msg=null;
                    Socket client = serverListener.accept();
                    while (!client.isClosed()) {
                        ObjectInputStream bufferInput = new ObjectInputStream(client.getInputStream());
                        msg = (Message) bufferInput.readObject();
                        client.close();
                    }
                    if (msg != null) {
                        switch(msg.getType()){
                            case 0: System.out.println("Tabela de Vizinhos de "+getPort()+" : \n");
                                    vizinhos = (Map<Integer,Integer>) msg.getContent();
                                    for(Integer viz:vizinhos.keySet()){
                                       if(viz!=getPort()){
                                            System.out.println("Conhece Vizinho "+viz+" Porta "+vizinhos.get(viz));
                                        }
                                     }
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
    public void election() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        setCoodenador(1);//First process Coordenador
        vizinhos = new HashMap<Integer,Integer>();
        try {
            register();
            new Thread(this::updateListener).start();
            new Thread(this::election).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Started Process with id : "+getPid()+" and priority "+getPriority());
    }

    public int getCoodenador() {
        return coodenador;
    }

    public void setCoodenador(int coodenador) {
        this.coodenador = coodenador;
    }
}
