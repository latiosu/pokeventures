package engine.structs;

import objects.PlayerOnline;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserList implements List<PlayerOnline> {

    private Map<Long, PlayerOnline> map;
    private java.util.List<PlayerOnline> list;
    private PlayerOnline mainPlayer;

    public UserList() {
        map = new ConcurrentHashMap<>();
        list = new CopyOnWriteArrayList<>(); /* <-- Works well for small array lists */
    }

    public PlayerOnline getMainPlayer() {
        if (mainPlayer == null) {
            System.err.println("Error: Main player has not been set.");
        }
        return mainPlayer;
    }

    public void setMainPlayer(PlayerOnline p) {
        mainPlayer = p;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public PlayerOnline remove(int i) {
        return map.remove(list.remove(i));
    }

    @Override
    public PlayerOnline remove(long uid) {
        PlayerOnline result = get(uid);
        list.remove(result);
        return result;
    }

    @Override
    public PlayerOnline get(int i) {
        return list.get(i);
    }

    @Override
    public PlayerOnline get(long uid) {
        return map.get(uid);
    }

    @Override
    public boolean add(long uid, PlayerOnline player) {
        if (!contains(uid)) {
            map.put(uid, player);
            list.add(player);
        } else {
            System.err.println("Error: User already exists - " + player.getUsername());
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
    public Iterator<PlayerOnline> iterator() {
        return list.iterator();
    }
}
