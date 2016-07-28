import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by pedro-jorge on 27/07/2016.
 */
public class MasterApp {
    public static void main(String args[]){
        int actual_id;
        actual_id = 1;
        try {
            ServerSocket server = new ServerSocket(1234);
            while(!server.isClosed()){
                Socket client = server.accept();
                System.out.println("Nova conexão com o cliente " +
                        client.getPort()
                );
               PrintStream saida = new PrintStream(client.getOutputStream());
                saida.println(actual_id++);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
