package networking.packets;

public class PacketClientLogin extends Packet {

    private long uid;
    private String username;
    private float x, y;
    private int dir, type;
    private float hp, maxHp;
    private boolean isAlive;

    public PacketClientLogin(byte[] data) {
        super(PacketType.CLIENT_LOGIN.getId());
        String[] dataArray = readData(data).split(DL); // Cut first two chars and split
        this.uid = Long.parseLong(dataArray[0]);
        this.username = dataArray[1];
        this.x = Float.parseFloat(dataArray[2]);
        this.y = Float.parseFloat(dataArray[3]);
        this.dir = Integer.parseInt(dataArray[4]);
        this.type = Integer.parseInt(dataArray[5]);
        this.hp = Float.parseFloat(dataArray[6]);
        this.hp = Float.parseFloat(dataArray[7]);
        this.isAlive = Boolean.parseBoolean(dataArray[8]);
    }

    public PacketClientLogin(long uid, String username, float x, float y, int dir, int type,
                             float hp, float maxHp, boolean isAlive) {
        super(PacketType.CLIENT_LOGIN.getId());
        this.uid = uid;
        this.username = username;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.type = type;
        this.hp = hp;
        this.maxHp = maxHp;
        this.isAlive = isAlive;
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return (PacketType.CLIENT_LOGIN.getIdString() + this.uid +
                DL + this.username +
                DL + this.x +
                DL + this.y +
                DL + this.dir +
                DL + this.type +
                DL + this.hp +
                DL + this.maxHp +
                DL + this.isAlive).getBytes();
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

    public long getUid() {
        return uid;
    }

    public float getHp() {
        return hp;
    }

    public float getMaxHp() {
        return maxHp;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
