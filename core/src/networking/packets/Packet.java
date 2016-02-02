package networking.packets;

import engine.Logger;

public abstract class Packet {

    protected String DL = "`";
    public byte packetId;

    public Packet(int packetId) {
        this.packetId = (byte) packetId;
    }

    public static PacketType lookupPacket(String packetId) {
        try {
            return lookupPacket(Integer.parseInt(packetId));
        } catch (NumberFormatException e) {
            Logger.log(Logger.Level.ERROR, "Invalid packet type request\n");
            return PacketType.INVALID;
        }
    }

    public static PacketType lookupPacket(int id) {
        for (PacketType t : PacketType.values()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return PacketType.INVALID;
    }

    public abstract byte[] getData();

    public String readData(byte[] data) {
        String message = new String(data);
        return message.substring(2).trim(); // excludes packet id
    }

    public enum PacketType {
        INVALID(-1),
        CLIENT_LOGIN(0),
        SERVER_LOGIN(1),
        DISCONNECT(2),
        MOVE(3),
        ATTACK(4),
        PLAYER_STATE(5),
        CHAT(6),
        HEARTBEAT(7),
        EVENT(8),
        SCORES(9);

        private int packetId;

        PacketType(int packetId) {
            this.packetId = packetId;
        }

        public int getId() {
            return packetId;
        }

        public String getIdString() {
            return "0" + packetId;
        }
    }
}
