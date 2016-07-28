import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by pedro-jorge on 27/07/2016.
 */
public class MasterApp {
    public static void main(String args[]){
        int actual_id;
        actual_id = 1;
        ArrayList<Integer> portas_dos_clientes = new ArrayList<>();
        try {
            ServerSocket server = new ServerSocket(6345);
            new Thread(() -> {
                    while (true) {

                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        synchronized (portas_dos_clientes) {
                            for (Integer p : portas_dos_clientes) {
                                try{
                                    Socket client = new Socket("127.0.0.1", p);
                                    System.out.println("Sending Update to " + p);
                                    PrintStream saida = new PrintStream(client.getOutputStream());
                                    saida.println("Update Server " + p);
                                }catch (UnknownHostException e) {
                                     e.printStackTrace();
                                } catch (IOException e) {
                                     e.printStackTrace();
                            }
                            }
                        }
                    }

            }
            ).start();
            while(!server.isClosed()){
                Socket client = server.accept();
                System.out.println("Nova conexão com o cliente " +
                        client.getPort()
                );
                portas_dos_clientes.add(client.getPort());
               PrintStream saida = new PrintStream(client.getOutputStream());
                saida.println(actual_id++);
                saida.println(client.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
