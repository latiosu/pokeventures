package server;

import engine.Config;
import engine.Logger;
import engine.structs.AttackList;
import engine.structs.Event;
import engine.structs.Message;
import engine.structs.UserList;
import networking.packets.PacketAttack;
import networking.packets.PacketChat;
import networking.packets.PacketPlayerState;
import server.ServerThread;
import objects.BaseAttack;
import objects.BasePlayer;
import objects.structs.Direction;
import objects.structs.PlayerType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class ServerCore extends Thread {

    // Engine variables/constants
    public boolean isRunning = true;
    private long delta = 0;

    // Object classes
    private UserList players;
    private AttackList attacks;
    private List<Event> events;

    // Networking classes
    private ServerThread server;

    public ServerCore() {
        // Game world structures
        players = new UserList();
        attacks = new AttackList();
        events = new CopyOnWriteArrayList<>();

        // Start networking
        Logger.log(Logger.Level.INFO,
                "----- Server thread started -----\n");
        server = new ServerThread(this);
        server.start();

        // Attach clean up process
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                server.sendDataToAllClients(new PacketChat(
                        new Message("SERVER", "Host has closed server.\n")
                ));
                Logger.log(Logger.Level.INFO,
                        "----- Server has shutdown -----\n");
            }
        });
    }

    public void run() {
        while (isRunning) {
            long start = System.nanoTime();
            if (TimeUnit.NANOSECONDS.toSeconds(delta) > 1/30f) {
                checkConnections(); // Remove AFK players before updating game state
                updateAttacks();
                updateEvents(delta);
                delta -= TimeUnit.SECONDS.toNanos(1) * (1/30f);
            }
            delta += System.nanoTime() - start;
        }
    }

    private void checkConnections() {
        for (BasePlayer p : players) {
            // TODO: Check last packet received was not too long ago
        }
    }

    private void updateEvents(float delta) {
        List<Event> finished = new CopyOnWriteArrayList<>();
        // Update queued events first
        for (Event e : events) {
            // Finished events
            if (e.update(delta)) {
                finished.add(e);
            }
        }
        // Clean up finished events
        for (Event e : finished) {
            events.remove(e);
        }
    }

    /**
     * Each client checks for collisions with their own character and all existing projectiles.
     * Note: *May need to optimize with quadtree*
     */
    private void updateAttacks() {
        ArrayList<BaseAttack> toRemove = new ArrayList<>();

        // TODO: Optimize attack collision detection (Currently O(nm) runtime)
        for (BaseAttack atk : getAttacks()) {
            // Update position first
            atk.updatePosition();
            // Check for collisions with ALL PLAYERS
            for (BasePlayer p : getPlayers()) {
                if (atk.checkForCollision(p)) {
                    // Send update player state TO ALL PLAYERS
                    server.sendDataToAllClients(new PacketPlayerState(p.getUid(),
                            p.getUsername(),
                            p.getHp(),
                            p.getMaxHp(),
                            p.getState().getNum(),
                            p.getDirection().getNum(),
                            p.getType().getNum())
                    );
                    Logger.log(Logger.Level.INFO,
                            "%s was hit for %s damage (%s/%s)\n",
                            p.getUsername(),
                            atk.getDamage(),
                            p.getHp(),
                            p.getMaxHp());
                }
            }

            // Send updated attack state
            server.sendDataToAllClients(new PacketAttack(atk.getId(),
                    atk.getOwner().getUid(),
                    atk.getOwner().getType().getNum(),
                    atk.getDirection().getNum(),
                    atk.getPosX(),
                    atk.getPosY(),
                    atk.isAliveNum())
            );

            // Clean-up check
            if (!atk.isAlive()) {
                toRemove.add(atk);
            }
        }

        // Perform clean-up
        for (BaseAttack a : toRemove) {
            getAttacks().remove(a.getId());
        }
    }

    public void handlePlayerPacket(long uid, String username, float x, float y, float hp, float maxHp,
                                   objects.structs.State state, Direction dir, PlayerType type) {
        BasePlayer p = getPlayers().get(uid);
        if (p != null) {
            p.setX(x);
            p.setY(y);
            p.setHp(hp);
            p.setMaxHp(maxHp);
            p.setState(state);
            p.setDirection(dir);
            p.setType(type);
        } else {
            Logger.log(Logger.Level.ERROR,
                    "User not found - %s\n",
                    username);
        }
    }

    public void handleAttackPacket(long id, long uid, Direction dir, float x, float y, boolean isAlive) {
        BaseAttack atk = getAttacks().get(id);
        if (atk != null) {
            // Update attack if registered
            if (!atk.isAlive() || !isAlive) { // Remove if not alive
                getAttacks().remove(id);
            }
            // Else do nothing
        } else {
            // Store attack if not registered
            atk = new BaseAttack(id, getPlayers().get(uid), dir, x, y);
            attacks.add(id, atk);
        }
    }

    public synchronized UserList getPlayers() {
        return this.players;
    }

    public synchronized AttackList getAttacks() {
        return this.attacks;
    }
}
