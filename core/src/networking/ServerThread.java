package networking;

import engine.Core;
import networking.packets.Packet;
import networking.packets.Packet00Login;
import objects.Type;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {

    private DatagramSocket socket;
    private Core core;
    private List<PlayerMP> onlinePlayers = new ArrayList<PlayerMP>();

    public ServerThread(Core core) {
        this.core = core;
        try {
            this.socket = new DatagramSocket(4445); // listen on port 4445
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet); // Warning: will wait indefinitely
            } catch (IOException e) {
                e.printStackTrace();
            }
            parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
        }
    }

    public void sendData(byte[] data, InetAddress ip, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataToAllClients(byte[] data) {
        for(PlayerMP p : onlinePlayers){
            sendData(data, p.ip, p.port);
        }
    }

    private void parsePacket(byte[] data, InetAddress ip, int port) {
        String message = new String(data).trim();
        Packet.PacketType type = Packet.lookupPacket(message.substring(0, 2)); // first two characters
        switch (type) {
            default:
            case INVALID:
                break;
            case LOGIN:
                Packet00Login packet = new Packet00Login(data);
                System.out.printf("[%s:%d] %s has connected", ip.getHostAddress(), port, packet.getUsername());

                // Add new player to world
                PlayerMP player;
                if(ip.getHostAddress().equals("127.0.0.1")) {
//                    player = new PlayerMP(Type.CHARMANDER, );
                }
                break;
            case DISCONNECT:

                break;
        }
    }
}
