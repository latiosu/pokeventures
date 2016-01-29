package networking.threads;

import engine.Config;
import engine.structs.UserList;
import networking.packets.*;
import objects.Direction;
import objects.PlayerOnline;
import objects.PlayerType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerThread extends BasicThread {

    private UserList onlinePlayers;
    private SimpleDateFormat sdf;

    public ServerThread() {
        onlinePlayers = new UserList();
        sdf = new SimpleDateFormat(Config.DATE_FORMAT);
        try {
            this.socket = new DatagramSocket(Config.GAME_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    protected void parsePacket(byte[] data, InetAddress address, int port) {
        String message = new String(data).trim();
        Packet.PacketType type = Packet.lookupPacket(message.substring(0, 2)); // first two characters
        switch (type) {
            default:
            case INVALID:
                break;

            case LOGIN:
                Packet00Login lp = new Packet00Login(data);
                PlayerOnline p = new PlayerOnline(lp.getUID(), lp.getX(), lp.getY(),
                        Direction.getDir(lp.getDir()), PlayerType.getType(lp.getType()),
                        lp.getUsername(), address, port);
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

            case ATTACK:
                this.handleAttack(new Packet04Attack(data));
                break;
        }
    }

    public void addConnection(PlayerOnline newPlayer, Packet00Login pk) {
        boolean isConnected = false;
        for (PlayerOnline p : this.onlinePlayers) {
            if (newPlayer.getUID() == p.getUID()) {
                // Already connected and registered with server
                isConnected = true;
                if (p.getAddress() == null && p.getPort() == -1) {
                    p.setAddress(newPlayer.getAddress());
                    p.setPort(newPlayer.getPort());
                } else {
                    System.err.println("Error: Duplicate user login");
                }
            } else {
                // Notify selected player that a new player HAS JOINED
                this.sendData(pk.getData(), p.getAddress(), p.getPort());
                // Notify new player that selected player EXISTS
                Packet00Login lp = new Packet00Login(p.getUID(), p.getUsername(), p.getX(), p.getY(),
                        p.getDirection().getNum(), p.getType().getNum());
                this.sendData(lp.getData(), newPlayer.getAddress(), newPlayer.getPort());
            }
        }

        if (!isConnected) {
            // Add new player to online list
            this.onlinePlayers.add(newPlayer.getUID(), newPlayer);
        }
    }

    public void removeConnection(Packet01Disconnect pk) {
        onlinePlayers.remove(pk.getUID());
        pk.writeDataFrom(this);
    }

    /**
     * Note: Notifies all online players of a specific player's position data and direction.
     */
    private void handleMove(Packet02Move pk) {
        PlayerOnline p;
        if ((p = onlinePlayers.get(pk.getUID())) != null) {
            // Sync new data from player with other players
            p.setX(pk.getX());
            p.setY(pk.getY());
            p.setDirection(pk.getDir());
            p.setState(pk.getState());
            p.setType(pk.getType());
            Packet02Move newPacket = new Packet02Move(p.getUID(), p.getUsername(), p.getX(), p.getY(),
                    p.getState().getNum(), p.getDirection().getNum(), p.getType().getNum());
            newPacket.writeDataFrom(this); // Notify all users of new POSITION DATA
        }
    }

    /**
     * Note: Notifies all online players of new message.
     */
    private void handleChat(Packet03Chat pk) {
        pk.writeDataFrom(this); // Notify all users of new MESSAGE

        // Log message to console
        System.out.printf("[%s] %s: %s\n", getDate(pk.getTime()), pk.getUsername(), pk.getMessage());
    }

    private void handleAttack(Packet04Attack pk) {
        pk.writeDataFrom(this);
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
        for (PlayerOnline p : onlinePlayers) {
            sendData(data, p.getAddress(), p.getPort());
        }
    }

    private String getDate(long time) {
        return sdf.format(new Date(time));
    }

    public String getIpAddress() {
        String website = "http://checkip.amazonaws.com";
        try {
            URL whatIsMyIp = new URL(website);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatIsMyIp.openStream()));
            return in.readLine(); // Website returns HTML of your IP address
        } catch (MalformedURLException e) {
            System.err.printf("Are you sure this is a valid web address: %s\n", website);
        } catch (IOException e) {
            System.err.println("Couldn't reach the IP checking address properly.");
        }
        return null;
    }
}
