import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedro-jorge on 27/07/2016.
 */
public class MasterApp {
    public static void main(String args[]){
        int actual_id;
        actual_id = 1;
        Map<Integer,Integer> portas_dos_clientes = new HashMap<Integer,Integer>();
        try {
            ServerSocket server = new ServerSocket(6346);
            new Thread(() -> {
                    while (true) {

                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        synchronized (portas_dos_clientes) {//TODO caso não consiga conectar aquela porta retirar da lista.
                            int flag=0;
                            ArrayList<Integer> rm = new ArrayList<>();
                            for ( Integer id : portas_dos_clientes.keySet()) {
                                try{
                                    Socket client = new Socket("127.0.0.1", portas_dos_clientes.get(id));
                                    System.out.println("Sending Update to " + id);
                                    //PrintStream saida = new PrintStream(client.getOutputStream());
                                    //saida.println("Update Server " + p);
                                    ObjectOutputStream bufferStream = new ObjectOutputStream(client.getOutputStream());
                                    bufferStream.flush();
                                    Message msg = new Message(0,portas_dos_clientes);
                                    bufferStream.writeObject(msg);
                                    bufferStream.close();
                                }catch (UnknownHostException e) {
                                     e.printStackTrace();
                                }catch (IOException e) {
                                     flag =1;
                                    rm.add(id);
                                }
                            }
                            for(Integer id:rm) {
                                portas_dos_clientes.remove(id);
                            }
                        }
                    }

            }
            ).start();
            while(!server.isClosed()){//registraas
                Socket client = server.accept();
                System.out.println("Nova conexão com o cliente " +
                        client.getPort()
                );
                synchronized (portas_dos_clientes) {
                    portas_dos_clientes.put(actual_id, client.getPort());
                }
               PrintStream saida = new PrintStream(client.getOutputStream());
                saida.println(actual_id++);
                saida.println(client.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
