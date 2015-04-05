package networking.packets;

import networking.ClientThread;
import networking.ServerThread;

public class Packet00Login extends Packet {

    private long uid;
    private String username;
    private float x, y;
    private int dir, type;

    public Packet00Login(byte[] data) {
        super(00);
        String[] dataArray = readData(data).split(","); // Cut first two chars and split
        this.uid = Long.parseLong(dataArray[0]);
        this.username = dataArray[1];
        this.x = Float.parseFloat(dataArray[2]);
        this.y = Float.parseFloat(dataArray[3]);
        this.dir = Integer.parseInt(dataArray[4]);
        this.type = Integer.parseInt(dataArray[5]);
    }

    public Packet00Login(long uid, String username, float x, float y, int dir, int type) {
        super(00);
        this.uid = uid;
        this.username = username;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.type = type;
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
        return ("00" + this.uid + "," + this.username + "," + this.x + "," +
                this.y + "," + this.dir + "," + this.type).getBytes();
    }

    public String getUsername() {
        return this.username;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public int getDir() {
        return dir;
    }
    public int getType() {
        return type;
    }
    public long getUID() {
        return uid;
    }
}
