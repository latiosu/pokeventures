package networking.packets;

import networking.ClientThread;
import networking.ServerThread;

public class Packet00Login extends Packet {

    private String username;

    public Packet00Login(byte[] data) {
        super(00);
        this.username = readData(data);
    }

    public Packet00Login(String username) {
        super(00);
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
        return ("00" + this.username).getBytes();
    }

    public String getUsername() {
        return this.username;
    }
}
