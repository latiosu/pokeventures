package networking.packets;

import objects.structs.Direction;
import objects.structs.PlayerType;
import objects.structs.State;

public class PacketMove extends Packet {

    private long uid;
    private String username;
    private float x, y, hp, maxHp;
    private int state, dir, ptype;
    private boolean isAlive;

    public PacketMove(byte[] data) {
        super(PacketType.MOVE.getId());
        String[] dataArray = readData(data).split(DL);
        this.uid = Long.parseLong(dataArray[0]);
        this.username = dataArray[1];
        this.x = Float.parseFloat(dataArray[2]);
        this.y = Float.parseFloat(dataArray[3]);
        this.hp = Float.parseFloat(dataArray[4]);
        this.maxHp = Float.parseFloat(dataArray[5]);
        this.state = Integer.parseInt(dataArray[6]);
        this.dir = Integer.parseInt(dataArray[7]);
        this.ptype = Integer.parseInt(dataArray[8]);
        this.isAlive = Boolean.parseBoolean(dataArray[9]);
    }

    public PacketMove(long uid, String username, float x, float y, float hp, float maxHp,
                      int state, int dir, int ptype, boolean isAlive) {
        super(PacketType.MOVE.getId());
        this.uid = uid;
        this.username = username;
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.maxHp = maxHp;
        this.state = state;
        this.dir = dir;
        this.ptype = ptype;
        this.isAlive = isAlive;
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return (PacketType.MOVE.getIdString() +
                uid +
                DL + username +
                DL + x +
                DL + y +
                DL + hp +
                DL + maxHp +
                DL + state +
                DL + dir +
                DL + ptype +
                DL + isAlive
        ).getBytes();
    }

    public long getUid() {
        return uid;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getHp() {
        return hp;
    }

    public float getMaxHp() {
        return maxHp;
    }

    public State getState() {
        return State.getState(state);
    }

    public Direction getDir() {
        return Direction.getDir(dir);
    }

    public PlayerType getType() {
        return PlayerType.getType(ptype);
    }

    public String getUsername() {
        return username;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
