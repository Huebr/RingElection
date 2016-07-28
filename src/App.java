/**
 * Created by pedro-jorge on 27/07/2016.
 */
public class App {
    public static void main(String args[]){
        int number_of_process;
        number_of_process =5;
        while(number_of_process-- > 0){
            Process p = new Process();
            Thread t = new Thread(p);
            t.run();
        }
    }
}
