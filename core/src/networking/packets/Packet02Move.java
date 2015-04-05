package networking.packets;

import networking.ClientThread;
import networking.ServerThread;

public class Packet02Move extends Packet {

    private String username;
    private float x, y;
    private int isMoving, dir, type;

    public Packet02Move(byte[] data) {
        super(02);
        String[] dataArray = readData(data).split(","); // <----- forbidden in usernames
        this.username = dataArray[0];
        this.x = Float.parseFloat(dataArray[1]);
        this.y = Float.parseFloat(dataArray[2]);
        this.isMoving = Integer.parseInt(dataArray[3]);
        this.dir = Integer.parseInt(dataArray[4]);
        this.type = Integer.parseInt(dataArray[5]);
    }

    public Packet02Move(String username, float x, float y, int isMoving, int dir, int type) {
        super(02);
        this.username = username;
        this.x = x;
        this.y = y;
        this.isMoving = isMoving;
        this.dir = dir;
        this.type = type;
    }

    @Override
    public void writeData(ClientThread client) {
        client.sendData(getData());
    }

    @Override
    public void writeData(ServerThread server) {
        server.sendDataToAllClients(getData());
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return ("02" + this.username + "," + this.x + "," + this.y +
                "," + isMoving + "," + dir + "," + type).getBytes();
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
    public int isMoving() {
        return isMoving;
    }
    public int getDir() {
        return dir;
    }
    public int getType() {
        return type;
    }
}
