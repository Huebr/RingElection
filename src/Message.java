import java.io.Serializable;

/**
 * Created by Pedro on 28/07/2016.
 */
public class Message implements Serializable {
    final private int type;//0 update 1 election 2 elector 3 command from coodernator
    final private Object content;
    Message(int type,Object content){
        this.type=type;
        this.content=content;
    }

    public Object getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}
