import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by pedro-jorge on 27/07/2016.
 */
public class Process implements Runnable{
    private int pid;
    private int priority;
    private int x;
    ArrayList<Integer> vizinhos;

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
    public int register() throws IOException {
        Socket client = new Socket("127.0.0.1",6345); //Connection MasterApp
        Scanner s = new Scanner(client.getInputStream());
        int port = 0;
        while(!client.isClosed()){
            if(s.hasNextInt()) {
                setPid(s.nextInt());
                port = s.nextInt();
                client.close();
            }
        }
        return port;
    }
    public void updateListener(ArrayList<Integer> vizinhos,int port){
        try {
            ServerSocket serverListener = new ServerSocket(port);
            while(true){
                try {
                    Socket client = serverListener.accept();
                    while (!client.isClosed()) {
                        ObjectInputStream bufferInput = new ObjectInputStream(client.getInputStream());
                        vizinhos = (ArrayList<Integer>) bufferInput.readObject();
                        client.close();
                    }
                    System.out.println("Tabela de Vizinhos de "+port+" : \n");
                    for(Integer viz:vizinhos){
                        if(viz!=port){
                            System.out.println("Conhece Vizinho "+viz);
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        vizinhos = new ArrayList<>();
        try {
            int port = register();
            new Thread(()-> updateListener(vizinhos,port)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Started Process with id : "+getPid()+" and priority "+getPriority());
    }
}
