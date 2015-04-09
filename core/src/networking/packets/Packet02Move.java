package networking.packets;

import networking.ClientThread;
import networking.ServerThread;
import objects.Direction;
import objects.PlayerType;
import objects.State;
import objects.attacks.AttackType;

public class Packet02Move extends Packet {

    private long uid;
    private String username;
    private float x, y;
    private int state, dir, type;

    public Packet02Move(byte[] data) {
        super(02);
        String[] dataArray = readData(data).split("`");
        this.uid = Long.parseLong(dataArray[0]);
        this.username = dataArray[1];
        this.x = Float.parseFloat(dataArray[2]);
        this.y = Float.parseFloat(dataArray[3]);
        this.state = Integer.parseInt(dataArray[4]);
        this.dir = Integer.parseInt(dataArray[5]);
        this.type = Integer.parseInt(dataArray[6]);
    }

    public Packet02Move(long uid, String username, float x, float y, int state,
                        int dir, int type) {
        super(02);
        this.uid = uid;
        this.username = username;
        this.x = x;
        this.y = y;
        this.state = state;
        this.dir = dir;
        this.type = type;
    }

    @Override
    public void writeDataFrom(Thread thread) {
        if(thread instanceof ClientThread) {
            ((ClientThread) thread).sendData(getData());
        }
        else if (thread instanceof ServerThread)
            ((ServerThread) thread).sendDataToAllClients(getData());
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return ("02" + this.uid + "`" + this.username + "`" + this.x + "`" + this.y +
                "`" + state + "`" + dir + "`" + type).getBytes();
    }
    public long getUID() {
        return uid;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public State getState() {
        return State.getState(state);
    }
    public Direction getDir() {
        return Direction.getDir(dir);
    }
    public PlayerType getType() {
        return PlayerType.getType(type);
    }
    public String getUsername() {
        return username;
    }
}
