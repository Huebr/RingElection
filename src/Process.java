import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by pedro-jorge on 27/07/2016.
 */
public class Process implements Runnable{
    private int pid;
    private int priority;
    private int x;

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
    public void register() throws IOException {
        Socket client = new Socket("127.0.0.1",1234); //Connection MasterApp
        Scanner s = new Scanner(client.getInputStream());
        while(!client.isClosed()){
            if(s.hasNextInt()) {
                setPid(s.nextInt());
                client.close();
            }
        }
    }
    @Override
    public void run() {
        this.x=0;
        try {
            register();
            //updateList
            //doyourwork

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Started Process with id : "+getPid()+" and priority "+getPriority());
    }
}
