package networking.packets;


public abstract class Packet {

    public static enum PacketType {
        INVALID(-1),
        LOGIN(00),
        DISCONNECT(01),
        MOVE(02),
        CHAT(03);

        private int packetId;
        private PacketType(int packetId) {
            this.packetId = packetId;
        }
        public int getId() {
            return packetId;
        }
    }

    public byte packetId;
    public Packet(int packetId) {
        this.packetId = (byte) packetId;
    }

    public abstract void writeDataFrom(Thread thread);
    public abstract byte[] getData();

    public String readData(byte[] data) {
        String message = new String(data);
        return message.substring(2).trim(); // excludes packet id
    }

    public static PacketType lookupPacket(String packetId) {
        try {
            return lookupPacket(Integer.parseInt(packetId));
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid packet type request.");
            return PacketType.INVALID;
        }
    }

    public static PacketType lookupPacket(int id) {
        for(PacketType t : PacketType.values()) {
            if(t.getId() == id){
                return t;
            }
        }
        return PacketType.INVALID;
    }
}
