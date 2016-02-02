package networking.packets;

public class PacketDisconnect extends Packet {

    private long uid;

    public PacketDisconnect(byte[] data) {
        super(PacketType.DISCONNECT.getId());
        this.uid = Long.parseLong(readData(data));
    }

    public PacketDisconnect(long uid) {
        super(PacketType.DISCONNECT.getId());
        this.uid = uid;
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return (PacketType.DISCONNECT.getIdString() + this.uid).getBytes();
    }

    public long getUid() {
        return this.uid;
    }
}
