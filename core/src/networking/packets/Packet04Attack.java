package networking.packets;

import networking.ClientThread;
import networking.ServerThread;
import objects.Direction;
import objects.attacks.AttackType;

public class Packet04Attack extends Packet {

    private long pid, tid;
    private float x, y;
    private int dir, atkType;

    public Packet04Attack(byte[] data) {
        super(02);
        String[] dataArray = readData(data).split("`");
        this.pid = Long.parseLong(dataArray[0]);
        this.x = Float.parseFloat(dataArray[1]);
        this.y = Float.parseFloat(dataArray[2]);
        this.dir = Integer.parseInt(dataArray[3]);
        this.atkType = Integer.parseInt(dataArray[4]);
        this.tid = Long.parseLong(dataArray[5]);
    }

    public Packet04Attack(long pid, float x, float y, int dir, int atkType, long tid) {
        super(02);
        this.pid = pid;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.atkType = atkType;
        this.tid = tid;
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
        return ("02" + this.pid + "`" + this.x + "`" + this.y +
                "`" + this.dir + "`" + this.atkType + "`" + this.tid).getBytes();
    }

    public long getPID() {
        return pid;
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
}
