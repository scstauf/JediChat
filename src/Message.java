import java.io.*;

public class Message implements Serializable {
    protected static final long serialVersionUID = 1112122200L;
    private String message;
    private int type;

    public static final int MESSAGE = 0, DISCONNECT = 1;

    public Message(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int get_type() {
        return type;
    }

    public String get_message() {
        return message;
    }
}
