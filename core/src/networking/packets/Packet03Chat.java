package networking.packets;

import engine.structs.Message;
import networking.ClientThread;
import networking.ServerThread;

import java.util.Date;

public class Packet03Chat extends Packet {

    private long time;
    private String username;
    private String message;

    public Packet03Chat(byte[] data) {
        super(03);
        String[] dataArray = readData(data).split(",");
        this.time = Long.parseLong(dataArray[0]);
        this.username = dataArray[1];
        this.message = dataArray[2];
    }

    public Packet03Chat(Message msg) {
        this(msg.time, msg.username, msg.message);
    }

    public Packet03Chat(String username, String message) {
        this(new Date().getTime(), username, message);
    }

    public Packet03Chat(long time, String username, String message) {
        super(03);
        this.time = time;
        this.username = username;
        this.message = message;
    }

    @Override
    public void writeDataFrom(Thread thread) {
        if(thread instanceof ClientThread)
            ((ClientThread) thread).sendData(getData());
        else if (thread instanceof ServerThread)
            ((ServerThread) thread).sendDataToAllClients(getData());
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return ("03" + this.time + "," + this.username + "," + this.message).getBytes();
    }
    public long getTime() {
        return this.time;
    }
    public String getUsername() {
        return this.username;
    }
    public String getMessage() {
        return this.message;
    }
}
