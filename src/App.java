/**
 * Created by pedro-jorge on 27/07/2016.
 */
public class App {//launcher dos processos.
    public static void main(String args[]){
            Process p = new Process();
            Thread t = new Thread(p);
            t.run();
    }
}
