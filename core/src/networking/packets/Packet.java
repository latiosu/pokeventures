package networking.packets;

import networking.ClientThread;
import networking.ServerThread;

public abstract class Packet {

    public static enum PacketType {
        INVALID(-1),
        LOGIN(00),
        DISCONNECT(01);

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

    public abstract void writeData(ClientThread client);
    public abstract void writeData(ServerThread server);
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
