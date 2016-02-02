package networking.packets;

import objects.structs.Direction;
import objects.structs.PlayerType;
import objects.structs.State;

public class PacketPlayerState extends Packet {

    private long uid;
    private String username;
    private float hp, maxHp;
    private int state, dir, ptype, score;

    public PacketPlayerState(byte[] data) {
        super(PacketType.PLAYER_STATE.getId());
        String[] dataArray = readData(data).split(DL);
        this.uid = Long.parseLong(dataArray[0]);
        this.username = dataArray[1];
        this.hp = Float.parseFloat(dataArray[2]);
        this.maxHp = Float.parseFloat(dataArray[3]);
        this.state = Integer.parseInt(dataArray[4]);
        this.dir = Integer.parseInt(dataArray[5]);
        this.ptype = Integer.parseInt(dataArray[6]);
        this.score = Integer.parseInt(dataArray[7]);
    }

    public PacketPlayerState(long uid, String username, float hp, float maxHp,
                             int state, int dir, int ptype, int score) {
        super(PacketType.PLAYER_STATE.getId());
        this.uid = uid;
        this.username = username;
        this.hp = hp;
        this.maxHp = maxHp;
        this.state = state;
        this.dir = dir;
        this.ptype = ptype;
        this.score = score;
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return (PacketType.PLAYER_STATE.getIdString() +
                uid +
                DL + username +
                DL + hp +
                DL + maxHp +
                DL + state +
                DL + dir +
                DL + ptype +
                DL + score
        ).getBytes();
    }

    public long getUid() {
        return uid;
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

    public int getScore() {
        return score;
    }
}
