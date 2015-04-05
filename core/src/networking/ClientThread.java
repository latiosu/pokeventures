package networking;

import engine.Config;
import engine.Core;
import engine.structs.Message;
import engine.structs.TimeComparator;
import networking.packets.*;
import objects.Entity;
import objects.Player;
import objects.PlayerOnline;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

public class ClientThread extends Thread {

    private InetAddress address;
    private DatagramSocket socket;
    private Core core;
    private Queue<Message> messages;
    private SimpleDateFormat sdf;

    public ClientThread(Core core, String ip) {
        this.core = core;
        messages = new PriorityQueue<Message>(Config.MESSAGES_INIT, new TimeComparator());
        sdf = new SimpleDateFormat(Config.DATE_FORMAT);
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
                handleLogin(new Packet00Login(data), address, port);
                break;
            case DISCONNECT:
                handleDisconnect(new Packet01Disconnect(data));
                break;
            case MOVE:
                handleMove(new Packet02Move(data));
                break;
            case CHAT:
                handleChat(new Packet03Chat(data));
                break;
        }
    }

    private void handleLogin(Packet00Login packet, InetAddress address, int port) {
        PlayerOnline player = new PlayerOnline(packet.getUID(), packet.getX(), packet.getY(), Entity.Direction.getDir(packet.getDir()),
                Entity.Type.getType(packet.getType()), false, packet.getUsername(), address, port);
         core.getPlayers().add(packet.getUID(), player); // <------- Attempt to add player to world
    }

    /* Assumes player exists in Core.players list */
    private void handleDisconnect(Packet01Disconnect packet) {
        int index = 0;
        for(Player p : core.getPlayers()){
            if(p.getUID()==packet.getUID()) {
                break;
            }
            index++;
        }
        core.getPlayers().remove(index);
    }

    private void handleMove(Packet02Move packet) {
        // Converts integer values from packet to standard types
        core.updatePlayer(packet.getUID(), packet.getUsername(), packet.getX(), packet.getY(), packet.isMoving()==1,
                Entity.Direction.getDir(packet.getDir()), Entity.Type.getType(packet.getType()));
    }

    /**
     * Note: DOES NOT REPLY TO SERVER
     */
    private void handleChat(Packet03Chat packet) {
        messages.add(new Message(packet.getTime(), packet.getUsername(), packet.getMessage()));

        // <-------------- RENDER CHAT HERE
        System.out.printf("CLIENT=[%s] %s: %s\n", getDate(packet.getTime()), packet.getUsername(), packet.getMessage());
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, Config.GAME_PORT); // Send data to server
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDate(long time) {
        return sdf.format(new Date(time));
    }
}
