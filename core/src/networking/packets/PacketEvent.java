package networking.packets;

import objects.structs.EventId;

public class PacketEvent extends Packet {

    private long uid;
    private int eventId;

    public PacketEvent(byte[] data) {
        super(PacketType.EVENT.getId());
        String[] dataArray = readData(data).split(DL);
        this.uid = Long.parseLong(dataArray[0]);
        this.eventId = Integer.parseInt(dataArray[1]);
    }

    public PacketEvent(long uid, int eventId) {
        super(PacketType.EVENT.getId());
        this.uid = uid;
        this.eventId = eventId;
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return (PacketType.EVENT.getIdString() +
                this.uid +
                DL + this.eventId
        ).getBytes();
    }

    public long getUid() {
        return this.uid;
    }

    public EventId getEventId() {
        return EventId.getEvent(eventId);
    }
}
