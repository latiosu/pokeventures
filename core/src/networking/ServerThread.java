package networking;

import engine.Config;
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
            this.socket = new DatagramSocket(Config.PORT); // listen on port 4445
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
                System.out.println("Server: Awaiting packet ...");
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
                String username = ((Packet00Login)packet).getUsername();
                System.out.printf("[%s:%d] %s has connected.\n", address.getHostAddress(), port, username);

                // Add new player to world
                PlayerMP player = new PlayerMP(Type.CHARMANDER, false, username, address, port);
                this.addConnection(player, (Packet00Login) packet);

                // Previously had init main player

                break;
            case DISCONNECT:

                break;
        }
    }

    public void addConnection(PlayerMP newPlayer, Packet00Login packet) {
        boolean isConnected = false;
        for(PlayerMP p : this.onlinePlayers) {
            if(newPlayer.getUsername().equalsIgnoreCase(p.getUsername())) {
                isConnected = true;
                if(p.address == null) {
                    p.address = newPlayer.address;
                }
                if(p.port == -1) {
                    p.port = newPlayer.port;
                }
            } else {
                // Notify selected player that a new player HAS JOINED
                this.sendData(packet.getData(), p.address, p.port);
                // Notify new player that selected player EXISTS
                packet = new Packet00Login(p.getUsername());
                this.sendData(packet.getData(), newPlayer.address, newPlayer.port);
            }
        }
        if(!isConnected) {
            this.onlinePlayers.add(newPlayer);
        }
    }

    public void sendData(byte[] data, InetAddress address, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port); // Send to online client
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataToAllClients(byte[] data) {
        for(PlayerMP p : onlinePlayers){
            sendData(data, p.address, p.port);
        }
    }

    // CUSTOM CORE COUPLER
    public List<PlayerMP> getOnlinePlayers() {
        return onlinePlayers;
    }
}
