package networking;

import engine.Config;
import engine.Core;
import engine.structs.Message;
import networking.packets.*;
import objects.Entity;
import objects.Player;
import objects.PlayerOnline;

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
        Packet.PacketType type = Packet.lookupPacket(message.substring(0, 2));
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

    private void handleLogin(Packet00Login p, InetAddress address, int port) {
        PlayerOnline player = new PlayerOnline(p.getUID(), p.getX(), p.getY(),
                Entity.Direction.getDir(p.getDir()), Entity.Type.getType(p.getType()),
                false, p.getUsername(), address, port);

        core.getPlayers().add(p.getUID(), player);
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
        core.updatePlayer(packet.getUID(), packet.getUsername(), packet.getX(), packet.getY(),
                packet.isMoving(), packet.getDir(), packet.getType());
    }

    /**
     * Note: DOES NOT REPLY TO SERVER
     */
    private void handleChat(Packet03Chat packet) {
        core.storeMsg(new Message(packet.getTime(), packet.getUsername(), packet.getMessage()));
    }

    /**
     * Sends given data to the server.
     */
    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, Config.GAME_PORT);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
