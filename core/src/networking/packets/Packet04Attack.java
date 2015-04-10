package networking.packets;

import networking.ClientThread;
import networking.ServerThread;
import objects.Direction;
import objects.PlayerType;
import objects.attacks.AttackType;

public class Packet04Attack extends Packet {

    private long id, uid;
    private float x, y;
    private int ptype, dir, atkType, alive;

    public Packet04Attack(byte[] data) {
        super(4);
        String[] dataArray = readData(data).split("`");
        this.id = Long.parseLong(dataArray[0]);
        this.uid = Long.parseLong(dataArray[1]);
        this.ptype = Integer.parseInt(dataArray[2]);
        this.dir = Integer.parseInt(dataArray[3]);
        this.x = Float.parseFloat(dataArray[4]);
        this.y = Float.parseFloat(dataArray[5]);
        this.atkType = Integer.parseInt(dataArray[6]);
        this.alive = Integer.parseInt(dataArray[7]);
    }

    public Packet04Attack(long id, long uid, int ptype, int dir, float x, float y, int atkType, int alive) {
        super(4);
        this.id = id;
        this.uid = uid;
        this.ptype = ptype;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.atkType = atkType;
        this.alive = alive;
    }

    @Override
    public void writeDataFrom(Thread thread) {
        if (thread instanceof ClientThread) {
            ((ClientThread) thread).sendData(getData());
        } else if (thread instanceof ServerThread)
            ((ServerThread) thread).sendDataToAllClients(getData());
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return ("04" + this.id + "`" + this.uid + "`" + this.ptype + "`" + this.dir + "`" + this.x + "`" + this.y
                 + "`" + this.atkType + "`" + this.alive).getBytes();
    }

    public long getId() {
        return id;
    }

    public long getUid() {
        return uid;
    }

    public PlayerType getPtype() {
        return PlayerType.getType(ptype);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Direction getDir() {
        return Direction.getDir(dir);
    }

    public AttackType getAtkType() {
        return AttackType.getType(atkType);
    }

    public boolean isAlive() {
        return alive==1;
    }
}
