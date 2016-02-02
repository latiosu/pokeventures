package networking.packets;

public class PacketHeartbeat extends Packet {

    private long uid;

    public PacketHeartbeat(byte[] data) {
        super(PacketType.HEARTBEAT.getId());
        this.uid = Long.parseLong(readData(data));
    }

    public PacketHeartbeat(long uid) {
        super(PacketType.HEARTBEAT.getId());
        this.uid = uid;
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return (PacketType.HEARTBEAT.getIdString() + this.uid).getBytes();
    }

    public long getUid() {
        return this.uid;
    }
}
