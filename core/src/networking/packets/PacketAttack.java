package networking.packets;

import objects.structs.Direction;
import objects.structs.PlayerType;

public class PacketAttack extends Packet {

    private long id, uid;
    private float x, y;
    private int ptype, dir, alive;

    public PacketAttack(byte[] data) {
        super(PacketType.ATTACK.getId());
        String[] dataArray = readData(data).split(DL);
        this.id = Long.parseLong(dataArray[0]);
        this.uid = Long.parseLong(dataArray[1]);
        this.ptype = Integer.parseInt(dataArray[2]);
        this.dir = Integer.parseInt(dataArray[3]);
        this.x = Float.parseFloat(dataArray[4]);
        this.y = Float.parseFloat(dataArray[5]);
        this.alive = Integer.parseInt(dataArray[6]);
    }

    public PacketAttack(long id, long uid, int ptype, int dir, float x, float y, int alive) {
        super(PacketType.ATTACK.getId());
        this.id = id;
        this.uid = uid;
        this.ptype = ptype;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.alive = alive;
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return (PacketType.ATTACK.getIdString()
                + this.id +
                DL + this.uid +
                DL + this.ptype +
                DL + this.dir +
                DL + this.x +
                DL + this.y +
                DL + this.alive
        ).getBytes();
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

    public boolean isAlive() {
        return alive == 1;
    }
}
