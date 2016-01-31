package networking.packets;

public class PacketServerLogin extends Packet {

    private long uid;
    private String username;
    private int type;

    public PacketServerLogin(byte[] data) {
        super(PacketType.SERVER_LOGIN.getId());
        String[] dataArray = readData(data).split(DL); // Cut first two chars and split
        this.uid = Long.parseLong(dataArray[0]);
        this.username = dataArray[1];
        this.type = Integer.parseInt(dataArray[2]);
    }

    public PacketServerLogin(long uid, String username, int type) {
        super(PacketType.SERVER_LOGIN.getId());
        this.uid = uid;
        this.username = username;
        this.type = type;
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return (PacketType.SERVER_LOGIN.getIdString() +
                this.uid +
                DL + this.username +
                DL + this.type
        ).getBytes();
    }

    public String getUsername() {
        return this.username;
    }

    public int getType() {
        return type;
    }

    public long getUid() {
        return uid;
    }

}
