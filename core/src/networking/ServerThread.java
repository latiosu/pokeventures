package networking;

import engine.Config;
import engine.Core;
import networking.packets.Packet;
import networking.packets.Packet00Login;
import networking.packets.Packet01Disconnect;
import networking.packets.Packet02Move;
import objects.Direction;
import objects.PlayerOnline;
import objects.Type;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {

    private DatagramSocket socket;
    private Core core;
    private List<PlayerOnline> onlinePlayers = new ArrayList<PlayerOnline>();

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
            byte[] data = new byte[Config.PACKET_SIZE];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
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
        switch (type) {
            default:
            case INVALID:
                break;
            case LOGIN:
                Packet00Login loginPacket = new Packet00Login(data);
                System.out.printf("[%s:%d] %s has connected. Online: %d\n",
                        address.getHostAddress(), port, loginPacket.getUsername(), onlinePlayers.size());

                // Add new player to world
                PlayerOnline player = new PlayerOnline(loginPacket.getX(), loginPacket.getY(),
                        Direction.getDir(loginPacket.getDir()), Type.getType(loginPacket.getType()),
                        false, loginPacket.getUsername(), address, port);
                this.addConnection(player, loginPacket);
                break;
            case DISCONNECT:
                Packet01Disconnect packet = new Packet01Disconnect(data);
                System.out.printf("[%s:%d] %s has disconnected. Online: %d\n",
                        address.getHostAddress(), port, packet.getUsername(), onlinePlayers.size());
                this.removeConnection(packet);
                break;
            case MOVE:
                Packet02Move movePacket = new Packet02Move(data);
                this.handleMove(movePacket);
                break;
        }
    }

    public void addConnection(PlayerOnline newPlayer, Packet00Login packet) {
        boolean isConnected = false;
        for(PlayerOnline p : this.onlinePlayers) {
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
                packet = new Packet00Login(p.getUsername(), p.getX(), p.getY(),
                        p.getDirection().getNum(), p.getType().getNum());
                this.sendData(packet.getData(), newPlayer.address, newPlayer.port);
            }
        }
        if(!isConnected) {
            this.onlinePlayers.add(newPlayer);
        }
    }

    public void removeConnection(Packet01Disconnect packet) {
        this.onlinePlayers.remove(getPlayerIndex(packet.getUsername()));
        packet.writeData(this);
    }

    private void handleMove(Packet02Move packet) {
        int index = getPlayerIndex(packet.getUsername());
        if(index != -1) {
            PlayerOnline p = this.onlinePlayers.get(index);
            // Update data stored on server
            p.setX(packet.getX());
            p.setY(packet.getY());
            p.setMoving(packet.isMoving());
            p.setDirection(Direction.getDir(packet.getDir()));
            packet.writeData(this); // Notify all users of new positions
        }
    }

    public int getPlayerIndex(String username) {
        int index = -1;
        for(PlayerOnline p : this.onlinePlayers) {
            index++;
            if(p.getUsername().equalsIgnoreCase(username)) {
                break;
            }
        }
        return index;
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
        for(PlayerOnline p : onlinePlayers){
            sendData(data, p.address, p.port);
        }
    }
}
