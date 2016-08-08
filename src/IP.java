import java.io.Serializable;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Pedro on 08/08/2016.
 */
public class IP implements Serializable{
    private String ip;
    private int  port;
    IP(String ip,int port){
        setIp(ip);
        setPort(port);
    }
    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
