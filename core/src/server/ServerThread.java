package server;

import engine.Config;
import engine.Logger;
import engine.structs.Message;
import networking.BasicThread;
import networking.packets.*;
import objects.BaseAttack;
import objects.BasePlayer;
import objects.structs.PlayerType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class ServerThread extends BasicThread {

    private ServerCore core;

    public ServerThread(ServerCore core) {
        this.core = core;
        try {
            this.socket = new DatagramSocket(Config.Networking.GAME_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Logger.log(Logger.Level.INFO,
                "Server is running on: %s:%s\n",
                getIpAddress(),
                Config.Networking.GAME_PORT);
    }

    protected void parsePacket(byte[] data, InetAddress address, int port) {
        String message = new String(data).trim();
        Packet.PacketType type = Packet.lookupPacket(message.substring(0, 2)); // First two characters
        switch (type) {
            default:

            case INVALID:
                break;

            case SERVER_LOGIN:
                PacketServerLogin lp = new PacketServerLogin(data);
                BasePlayer newPlayer = new BasePlayer(lp.getUid(),
                        lp.getUsername(),
                        PlayerType.getType(lp.getType())
                );
                // Manually set port and IP address
                newPlayer.setAddress(address);
                newPlayer.setPort(port);

                // Update player registration with all other players
                boolean isConnected = false;
                for (BasePlayer p : core.getPlayers()) {
                    if (newPlayer.getUid() == p.getUid()) { // If connected and registered with server
                        isConnected = true;
                        // Bugfix: I forget what it was for @___@
                        if (p.getAddress() == null && p.getPort() == -1) {
                            p.setAddress(newPlayer.getAddress());
                            p.setPort(newPlayer.getPort());
                        } else {
                            Logger.log(Logger.Level.ERROR,
                                    "Duplicate user login (%s)\n",
                                    lp.getUsername()
                            );
                        }
                    } else {
                        // Notify selected player that this new player HAS JOINED
                        PacketClientLogin newPlayerPk = new PacketClientLogin(newPlayer.getUid(),
                                newPlayer.getUsername(),
                                newPlayer.getX(),
                                newPlayer.getY(),
                                newPlayer.getDirection().getNum(),
                                newPlayer.getType().getNum(),
                                newPlayer.getHp(),
                                newPlayer.getMaxHp(),
                                newPlayer.isAlive()
                        );
                        sendDataToClient(newPlayerPk, p.getAddress(), p.getPort());
                        // Notify new player that this selected player EXISTS
                        PacketClientLogin selectedPlayerPk = new PacketClientLogin(p.getUid(),
                                p.getUsername(),
                                p.getX(),
                                p.getY(),
                                p.getDirection().getNum(),
                                p.getType().getNum(),
                                p.getHp(),
                                p.getMaxHp(),
                                p.isAlive()
                        );
                        sendDataToClient(selectedPlayerPk, newPlayer.getAddress(), newPlayer.getPort());
                        sendDataToClient(new PacketChat(
                                new Message("SERVER", newPlayer.getUsername() + " has appeared!")),
                                p.getAddress(),
                                p.getPort()
                        );
                    }
                }
                if (!isConnected) {
                    // Add new player to online list
                    core.getPlayers().add(newPlayer.getUid(), newPlayer);
                }
                Logger.log(Logger.Level.INFO,
                        "(%s:%d) %s has connected. Online: %d\n",
                        address.getHostAddress(),
                        port,
                        newPlayer.getUsername(),
                        core.getPlayers().size()
                );

                // Send welcome message to new player
                sendDataToClient(new PacketChat(
                        new Message("SERVER", "Welcome to Pokeventures!\n<< Chat with the Enter key >>")),
                        newPlayer.getAddress(),
                        newPlayer.getPort()
                );
                break;

            case DISCONNECT:
                PacketDisconnect dp = new PacketDisconnect(data);
                BasePlayer dcPlayer = core.getPlayers().remove(dp.getUid());

                // Notify all other players
                sendDataToAllOtherClients(dp.getUid(), dp);
                sendDataToAllOtherClients(dp.getUid(), new PacketChat(
                        new Message("SERVER", dcPlayer.getUsername() + " has fled."))
                );

                Logger.log(Logger.Level.INFO,
                        "(%s:%d) %s has fled. Online: %d\n",
                        address.getHostAddress(),
                        port,
                        core.getPlayers().get(dp.getUid()).getUsername(),
                        core.getPlayers().size()
                );
                break;

            case MOVE:
                PacketMove mp = new PacketMove(data);
                if ((newPlayer = core.getPlayers().get(mp.getUid())) != null) {
                    // Sync new data from player with all other players
                    newPlayer.setX(mp.getX());
                    newPlayer.setY(mp.getY());
                    newPlayer.setHp(mp.getHp());
                    newPlayer.setMaxHp(mp.getMaxHp());
                    newPlayer.setDirection(mp.getDir());
                    newPlayer.setState(mp.getState());
                    newPlayer.setType(mp.getType());
                    newPlayer.setAlive(mp.isAlive());

                    PacketMove newPacket = new PacketMove(newPlayer.getUid(),
                            newPlayer.getUsername(),
                            newPlayer.getX(),
                            newPlayer.getY(),
                            newPlayer.getHp(),
                            newPlayer.getMaxHp(),
                            newPlayer.getState().getNum(),
                            newPlayer.getDirection().getNum(),
                            newPlayer.getType().getNum(),
                            newPlayer.isAlive()
                    );
                    sendDataToAllOtherClients(mp.getUid(), newPacket); // Notify all OTHER users of new POSITION DATA
                }
                break;

            case CHAT:
                PacketChat cp = new PacketChat(data);
                sendDataToAllClients(cp); // Notify all users of new MESSAGE
                Logger.log(Logger.Level.INFO,
                        "%s: %s\n",
                        cp.getUsername(),
                        cp.getMessage()
                );
                break;

            case ATTACK:
                PacketAttack ap = new PacketAttack(data);
                BaseAttack atk;
                if (core.getAttacks().contains(ap.getId())) { // Then update
                    atk = core.getAttacks().get(ap.getId());
                    atk.setX(ap.getX());
                    atk.setY(ap.getY());
                    atk.setAlive(ap.isAlive());
                    atk.setDirection(ap.getDir());
                } else {
                    atk = new BaseAttack(ap.getId(),
                            core.getPlayers().get(ap.getUid()),
                            ap.getDir(),
                            ap.getX(),
                            ap.getY()
                    );
                }
                core.getAttacks().add(ap.getId(), atk);
                sendDataToAllClients(ap); // Notify ALL users of attack position
                break;

            case HEARTBEAT:
                PacketHeartbeat hp = new PacketHeartbeat(data);
                core.getPlayers().get(hp.getUid()).updateLastPacketTime();
                break;
        }
    }

    public void sendDataToClient(Packet pk, InetAddress address, int port) {
        DatagramPacket packet = new DatagramPacket(pk.getData(), pk.getData().length, address, port); // Send to online client
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataToAllClients(Packet pk) {
        for (BasePlayer p : core.getPlayers()) {
            sendDataToClient(pk, p.getAddress(), p.getPort());
        }
    }

    public void sendDataToAllOtherClients(long uid, Packet pk) {
        for (BasePlayer p : core.getPlayers()) {
            if (p.getUid() != uid) {
                sendDataToClient(pk, p.getAddress(), p.getPort());
            }
        }
    }

    public String getIpAddress() {
        String website = "http://checkip.amazonaws.com";
        try {
            URL whatIsMyIp = new URL(website);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatIsMyIp.openStream()));
            return in.readLine(); // Website returns HTML of your IP address
        } catch (MalformedURLException e) {
            Logger.log(Logger.Level.ERROR,
                    "Couldn't access the following web address: %s\n",
                    website);
        } catch (IOException e) {
            Logger.log(Logger.Level.ERROR,
                    "There was a problem trying to retrieve the server IP address\n");
        }
        return null;
    }
}
