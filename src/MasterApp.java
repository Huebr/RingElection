import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * Created by pedro-jorge on 27/07/2016.
 */


public class MasterApp {
    static volatile private boolean closed=false;
    static private ServerSocket server;
    static private void close(){
        closed=true;
    }
    public static void main(String args[]){
        int actual_id;
        actual_id = 1;
        Map<Integer,IP> portas_dos_clientes = new HashMap<Integer,IP>();
        try {
            server = new ServerSocket(6346);
            Logger logger = Logger.getLogger("MasterApp.log");
            logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
            new Thread(()->{
              while (!closed) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (portas_dos_clientes) {
                    int flag=0;
                    ArrayList<Integer> rm = new ArrayList<>();
                    for ( Integer id : portas_dos_clientes.keySet()) {
                        try{
                            Socket client = new Socket();
                            int time=10000;
                            client.connect(new InetSocketAddress(portas_dos_clientes.get(id).getIp(), portas_dos_clientes.get(id).getPort()), time);
                            logger.info("Sending Update to " + id);
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
            }}).start();
            new Thread(()->{
                try {
                    TimeUnit.MINUTES.sleep(10);
                    closed=true;
                    server.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            while(!server.isClosed()){//registra
                Socket client = server.accept();
                logger.info("Nova conexao com o cliente " +
                                client.getInetAddress().getHostAddress() + " " + client.getPort()
                );
                synchronized (portas_dos_clientes) {
                    portas_dos_clientes.put(actual_id, new IP(client.getInetAddress().getHostAddress(), client.getPort()));
                }
               PrintStream saida = new PrintStream(client.getOutputStream());
                saida.println(actual_id++);
                saida.println(client.getPort());
                client.close();
            }
        } catch (IOException e) {
        }
    }
}
