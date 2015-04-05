package networking.packets;

import networking.ClientThread;
import networking.ServerThread;

public class Packet00Login extends Packet {

    private String username;
    private float x, y;
    private int dir, type;

    public Packet00Login(byte[] data) {
        super(00);
        String[] dataArray = readData(data).split(","); // Cut first two chars and split
        this.username = dataArray[0];
        this.x = Float.parseFloat(dataArray[1]);
        this.y = Float.parseFloat(dataArray[2]);
        this.dir = Integer.parseInt(dataArray[3]);
        this.type = Integer.parseInt(dataArray[4]);
    }

    public Packet00Login(String username, float x, float y, int dir, int type) {
        super(00);
        this.username = username;
        this.x = x;
        this.y = y;
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
    public byte[] getData() {
        return ("00" + this.username + "," + this.x + "," +
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
}
