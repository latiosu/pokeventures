package networking.threads;

import engine.Config;
import engine.Core;
import engine.structs.Message;
import networking.packets.*;
import objects.Direction;
import objects.Player;
import objects.PlayerOnline;
import objects.PlayerType;

import java.io.IOException;
import java.net.*;

public class ClientThread extends BasicThread {

    private InetAddress address;
    private Core core;

    public ClientThread(Core core, String ip) {
        this.core = core;
        try {
            this.socket = new DatagramSocket();
            this.address = InetAddress.getByName(ip);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    protected void parsePacket(byte[] data, InetAddress address, int port) {
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
            case ATTACK:
                handleAttack(new Packet04Attack(data));
                break;
        }
    }

    private void handleLogin(Packet00Login pk, InetAddress address, int port) {
        PlayerOnline player = new PlayerOnline(pk.getUID(), pk.getX(), pk.getY(),
                Direction.getDir(pk.getDir()), PlayerType.getType(pk.getType()),
                pk.getUsername(), address, port);

        core.getPlayers().add(pk.getUID(), player);
    }

    /* Assumes player exists in Core.players list */
    private void handleDisconnect(Packet01Disconnect pk) {
        int index = 0;
        for (Player p : core.getPlayers()) {
            if (p.getUID() == pk.getUID()) {
                break;
            }
            index++;
        }
        core.getPlayers().remove(index);
    }

    private void handleMove(Packet02Move pk) {
        core.updatePlayer(pk.getUID(), pk.getUsername(), pk.getX(), pk.getY(),
                pk.getState(), pk.getDir(), pk.getType());
    }

    /**
     * Note: DOES NOT REPLY TO SERVER
     */
    private void handleChat(Packet03Chat pk) {
        core.storeMsg(new Message(pk.getTime(), pk.getUsername(), pk.getMessage()));
    }

    private void handleAttack(Packet04Attack pk) {
        core.updateAttack(pk.getId(), pk.getUid(), pk.getPtype(), pk.getDir(), pk.getX(), pk.getY(), pk.isAlive());
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
