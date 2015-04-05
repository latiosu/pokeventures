package networking.packets;

import networking.ClientThread;
import networking.ServerThread;

public class Packet01Disconnect extends Packet {

    private String username;

    public Packet01Disconnect(byte[] data) {
        super(01);
        this.username = readData(data);
    }

    public Packet01Disconnect(String username) {
        super(01);
        this.username = username;
    }

    @Override
    public void writeData(ClientThread client) {
        client.sendData(getData());
    }

    @Override
    public void writeData(ServerThread server) {
        server.sendDataToAllClients(getData());
    }

    @Override
    public byte[] getData() {
        return ("01" + this.username).getBytes();
    }

    public String getUsername() {
        return this.username;
    }
}
