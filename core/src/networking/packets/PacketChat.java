package networking.packets;

import engine.structs.Message;

public class PacketChat extends Packet {

    private long time;
    private String username;
    private String message;

    public PacketChat(byte[] data) {
        super(PacketType.CHAT.getId());
        String[] dataArray = readData(data).split(DL);
        this.time = Long.parseLong(dataArray[0]);
        this.username = dataArray[1];
        this.message = dataArray[2];
    }

    public PacketChat(Message msg) {
        this(msg.time, msg.username, msg.message);
    }

    public PacketChat(long time, String username, String message) {
        super(PacketType.CHAT.getId());
        this.time = time;
        this.username = username;
        this.message = message;
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return (PacketType.CHAT.getIdString() +
                this.time +
                DL + this.username +
                DL + this.message
        ).getBytes();
    }

    public long getTime() {
        return this.time;
    }

    public String getUsername() {
        return this.username;
    }

    public String getMessage() {
        return this.message;
    }
}
