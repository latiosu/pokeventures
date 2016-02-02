package networking;

import engine.ClientCore;
import engine.Config;
import engine.Logger;
import engine.structs.Message;
import engine.structs.Score;
import networking.packets.*;
import objects.BasePlayer;
import objects.Player;
import objects.structs.Direction;
import objects.structs.PlayerType;

import java.io.IOException;
import java.net.*;

public class ClientThread extends BasicThread {

    private InetAddress address;
    private ClientCore clientCore;

    public ClientThread(ClientCore clientCore, String ip) {
        this.clientCore = clientCore;
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException | SocketException e) {
            Logger.log(Logger.Level.ERROR,
                    "Couldn't resolve server address (%s)\n",
                    Config.Networking.SERVER_IP);
        }
    }

    protected void parsePacket(byte[] data, InetAddress address, int port) {
        String message = new String(data).trim();
        Packet.PacketType type = Packet.lookupPacket(message.substring(0, 2));
        switch (type) {
            default:

            case INVALID:
                break;

            case CLIENT_LOGIN:
                PacketClientLogin lp = new PacketClientLogin(data);
                Player player = new Player(lp.getUid(),
                        lp.getX(),
                        lp.getY(),
                        Direction.getDir(lp.getDir()),
                        PlayerType.getType(lp.getType()),
                        lp.getUsername(),
                        lp.getHp(),
                        lp.getMaxHp(),
                        lp.isAlive());
                clientCore.getPlayers().add(lp.getUid(), player);
                break;

            case DISCONNECT:
                PacketDisconnect dp = new PacketDisconnect(data);
                int index = 0;
                for (BasePlayer p : clientCore.getPlayers()) {
                    if (p.getUid() == dp.getUid()) {
                        break;
                    }
                    index++;
                }
                clientCore.getPlayers().remove(index);
                break;

            case MOVE:
                PacketMove mp = new PacketMove(data);
                clientCore.handlePlayerPacket(mp.getUid(),
                        mp.getUsername(),
                        mp.getX(),
                        mp.getY(),
                        mp.getHp(),
                        mp.getMaxHp(),
                        mp.getState(),
                        mp.getDir(),
                        mp.getType());
                break;

            case CHAT:
                PacketChat cp = new PacketChat(data);
                clientCore.storeMsg(new Message(cp.getTime(), cp.getUsername(), cp.getMessage()));
                break;

            case ATTACK:
                PacketAttack ap = new PacketAttack(data);
                clientCore.handleAttackPacket(ap.getId(),
                        ap.getUid(),
                        ap.getDir(),
                        ap.getX(),
                        ap.getY(),
                        ap.isAlive());
                break;

            case PLAYER_STATE:
                PacketPlayerState psp = new PacketPlayerState(data);
                Player p = (Player) clientCore.getPlayers().get(psp.getUid());
                p.setHp(psp.getHp());
                p.setMaxHp(psp.getMaxHp());
                p.setState(psp.getState());
                p.setDirection(psp.getDir());
                p.setType(psp.getType());
                p.setScore(psp.getScore());
                break;

            case EVENT:
                PacketEvent pe = new PacketEvent(data);
                switch (pe.getEventId()) {
                    case LEVEL_UP:
                    case HEALTH_PICKUP:
                        clientCore.getEventManager().startEvent(
                                pe.getEventId(),
                                clientCore.getPlayers().get(pe.getUid())
                        );
                        break;
                    default:
                        Logger.log(Logger.Level.ERROR,
                                "EventId not recognised (%s)\n",
                                pe.getEventId());
                }
                break;

            case SCORES:
                PacketScores ps = new PacketScores(data);

                for (Score s : ps.getScores()) {
                    BasePlayer bp = clientCore.getPlayers().get(s.uid);
                    if (bp != null) {
                        bp.setScore(s.score);
                    }
                }

                // Update scoreboard text now
                clientCore.getUI().getScoreboard().updateScoreboardUI(ps.getScores());
                break;
        }
    }

    public void sendDataToServer(Packet pk) {
        DatagramPacket packet = new DatagramPacket(pk.getData(), pk.getData().length, address, Config.Networking.GAME_PORT);
        try {
            if (isConnected()) {
                socket.send(packet);
            }
        } catch (IOException e) {
            Logger.log(Logger.Level.ERROR,
                    "Couldn't send packet to server (IOException)\n");
        }
    }

    public boolean isConnected() {
        return !(socket == null || address == null);
    }
}
