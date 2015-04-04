package networking;

import engine.Config;
import engine.Core;
import networking.packets.Packet;
import networking.packets.Packet00Login;
import objects.Type;

import java.io.IOException;
import java.net.*;

public class ClientThread extends Thread {

    private InetAddress address;
    private DatagramSocket socket;
    private Core core;

    public ClientThread(Core core, String ip) {
        this.core = core;
        try {
            this.socket = new DatagramSocket();
            this.address = InetAddress.getByName(ip);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                System.out.println("Client: Awaiting packet ...");
                socket.receive(packet); // Warning: will wait indefinitely
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port) {
        String message = new String(data).trim();
        Packet.PacketType type = Packet.lookupPacket(message.substring(0, 2)); // first two characters
        Packet packet = null;
        switch (type) {
            default:
            case INVALID:
                break;
            case LOGIN:
                packet = new Packet00Login(data);
                handleLogin((Packet00Login) packet, address, port);
                break;
            case DISCONNECT:

                break;
        }
    }

    private void handleLogin(Packet00Login packet, InetAddress address, int port) {
        System.out.printf("[%s:%d] %s has joined the game.\n", address.getHostAddress(), port, packet.getUsername());
        PlayerMP player = new PlayerMP(Type.CHARMANDER, false, packet.getUsername(), address, port);
        core.players.add(player); // Add new player to world
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, Config.PORT); // Send data to server
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
