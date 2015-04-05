package networking;

import engine.Config;
import engine.Core;
import networking.packets.Packet;
import networking.packets.Packet00Login;
import networking.packets.Packet01Disconnect;
import networking.packets.Packet02Move;
import objects.Direction;
import objects.Player;
import objects.PlayerOnline;
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
        }
    }

    private void handleLogin(Packet00Login packet, InetAddress address, int port) {
        PlayerOnline player = new PlayerOnline(packet.getX(), packet.getY(), Direction.getDir(packet.getDir()),
                Type.getType(packet.getType()), false, packet.getUsername(), address, port);
         core.getPlayers().add(packet.getUsername(), player); // <------- Attempt to add player to world
    }

    /* Assumes player exists in Core.players list */
    private void handleDisconnect(Packet01Disconnect packet) {
        int index = 0;
        for(Player p : core.getPlayers()){
            if(p.getUsername().equalsIgnoreCase(packet.getUsername())) {
                break;
            }
            index++;
        }
        core.getPlayers().remove(index);
    }

    private void handleMove(Packet02Move packet) {
        // Converts integer values from packet to standard types
        core.updatePlayer(packet.getUsername(), packet.getX(), packet.getY(), packet.isMoving()==1,
                Direction.getDir(packet.getDir()), Type.getType(packet.getType()));
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
