package networking;

import engine.Config;
import engine.Core;
import engine.structs.Message;
import engine.structs.TimeComparator;
import engine.structs.UserList;
import networking.packets.*;
import objects.Entity;
import objects.PlayerOnline;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

public class ServerThread extends Thread {

    private DatagramSocket socket;
    private Core core;
    private engine.structs.List onlinePlayers = new UserList();
    private Queue<Message> messages;
    private SimpleDateFormat sdf;

    public ServerThread(Core core) {
        this.core = core;
        messages = new PriorityQueue<Message>(Config.MESSAGES_INIT, new TimeComparator());
        sdf = new SimpleDateFormat(Config.DATE_FORMAT);
        try {
            this.socket = new DatagramSocket(Config.GAME_PORT);
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
                Packet00Login lp = new Packet00Login(data);
                PlayerOnline p = new PlayerOnline(lp.getUID(), lp.getX(), lp.getY(),
                        Entity.Direction.getDir(lp.getDir()), Entity.Type.getType(lp.getType()),
                        false, lp.getUsername(), address, port);
                this.addConnection(p, lp);
                System.out.printf("[%s:%d] %s has connected. Online: %d\n",
                        address.getHostAddress(), port,
                        p.getUsername(), onlinePlayers.size());
                break;

            case DISCONNECT:
                Packet01Disconnect dp = new Packet01Disconnect(data);
                this.removeConnection(dp);
                System.out.printf("[%s:%d] %s has disconnected. Online: %d\n",
                        address.getHostAddress(), port,
                        onlinePlayers.get(dp.getUID()).getUsername(), onlinePlayers.size());
                break;

            case MOVE:
                this.handleMove(new Packet02Move(data));
                break;

            case CHAT:
                this.handleChat(new Packet03Chat(data));
                break;

        }
    }

    public void addConnection(PlayerOnline newPlayer, Packet00Login packet) {
        boolean isConnected = false;
        for(PlayerOnline p : this.onlinePlayers) {
            if(newPlayer.getUID() == p.getUID()) {
                // Already connected and registered with server
                isConnected = true;
                if(p.getAddress() == null && p.getPort() == -1) {
                    p.setAddress(newPlayer.getAddress());
                    p.setPort(newPlayer.getPort());
                } else {
                    System.err.println("Error: Duplicate user login");
                }
            } else {
                // Notify selected player that a new player HAS JOINED
                this.sendData(packet.getData(), p.getAddress(), p.getPort());
                // Notify new player that selected player EXISTS
                Packet00Login lp = new Packet00Login(p.getUID(), p.getUsername(), p.getX(), p.getY(),
                        p.getDirection().getNum(), p.getType().getNum());
                this.sendData(lp.getData(), newPlayer.getAddress(), newPlayer.getPort());
            }
        }

        if(!isConnected) {
            // Add new player to online list
            this.onlinePlayers.add(newPlayer.getUID(), newPlayer);
        }
    }

    public void removeConnection(Packet01Disconnect packet) {
        onlinePlayers.remove(packet.getUID());
        packet.writeDataFrom(this);
    }

    /**
     * Note: Notifies all online players of a specific player's position data and direction.
     */
    private void handleMove(Packet02Move packet) {
        PlayerOnline p;
        if((p = onlinePlayers.get(packet.getUID())) != null) {
            // Update data stored on server
            p.setX(packet.getX());
            p.setY(packet.getY());
            p.setMoving(packet.isMoving());
            p.setDirection(Entity.Direction.getDir(packet.getDir()));
            packet.writeDataFrom(this); // Notify all users of new POSITION DATA
        }
    }

    /**
     * Note: Notifies all online players of new message.
     */
    private void handleChat(Packet03Chat packet) {
        messages.add(new Message(packet.getTime(), packet.getUsername(), packet.getMessage()));
        packet.writeDataFrom(this); // Notify all users of new MESSAGE

        // Log message to console
        System.out.printf("SERVER=[%s] %s: %s\n", getDate(packet.getTime()), packet.getUsername(), packet.getMessage());
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
            sendData(data, p.getAddress(), p.getPort());
        }
    }

    private String getDate(long time) {
        return sdf.format(new Date(time));
    }
}
