package engine.structs;

import engine.Logger;
import objects.BasePlayer;
import objects.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserList implements List<BasePlayer> {

    private Map<Long, BasePlayer> map;
    private java.util.List<BasePlayer> list;
    private Player mainPlayer;

    public UserList() {
        map = new ConcurrentHashMap<>();
        list = new CopyOnWriteArrayList<>(); /* <-- Works well for small array lists */
    }

    public Player getMainPlayer() {
        if (mainPlayer == null) {
            Logger.log(Logger.Level.ERROR, "Main player has not been set\n");
        }
        return mainPlayer;
    }

    public void setMainPlayer(Player p) {
        mainPlayer = p;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public BasePlayer remove(int i) {
        return map.remove(list.remove(i));
    }

    @Override
    public BasePlayer remove(long uid) {
        BasePlayer result = get(uid);
        list.remove(result);
        return result;
    }

    @Override
    public BasePlayer get(int i) {
        return list.get(i);
    }

    /**
     * Retrieves the player with given uid from the map of players.
     *
     * @param uid - target player's uid
     * @return player if found, else null
     */
    @Override
    public BasePlayer get(long uid) {
        return map.get(uid);
    }

    @Override
    public boolean add(long uid, BasePlayer player) {
        if (!contains(uid)) {
            map.put(uid, player);
            list.add(player);
        } else {
            Logger.log(Logger.Level.ERROR,
                    "User already exists - %s\n",
                    player.getUsername());
            return false;
        }
        return true;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean contains(long uid) {
        return map.get(uid) != null;
    }

    @Override
    public Iterator<BasePlayer> iterator() {
        return list.iterator();
    }
}
