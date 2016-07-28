import java.io.IOException;
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
    public void updateListener(int port){
        try {
            ServerSocket serverListener = new ServerSocket(port);
            while(true){
                try {
                    Socket client = serverListener.accept();
                    Scanner sc = new Scanner(client.getInputStream());
                    while (!client.isClosed()) {
                        if (sc.hasNextLine()) {
                            System.out.println(sc.nextLine());
                            client.close();
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        this.x=0;
        try {
            int port = register();
            new Thread(()-> updateListener(port)).start();


        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Started Process with id : "+getPid()+" and priority "+getPriority());
    }
}
