package engine.structs;

import java.util.Date;

public class Message {

    public long time;
    public String username;
    public String message;

    public Message(String username, String message) {
        this(new Date().getTime(), username, message);
    }

    public Message(long time, String username, String message) {
        this.time = time;
        this.username = username;
        this.message = message;
    }
}
