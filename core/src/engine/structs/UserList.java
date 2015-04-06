package engine.structs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import objects.PlayerOnline;

public class UserList implements List {

    private Map<Long, PlayerOnline> map;
    private java.util.List<PlayerOnline> list;
    private PlayerOnline mainPlayer;

    public UserList() {
        map = new HashMap<Long, PlayerOnline>();
        list = new CopyOnWriteArrayList<PlayerOnline>(); /* <-- Works well for small array lists */
    }

    public PlayerOnline getMainPlayer() {
        if(mainPlayer == null){
            System.err.println("Error: Main player has not been set.");
        }
        return mainPlayer;
    }

    public void setMainPlayer(PlayerOnline p) {
        mainPlayer = p;
    }

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
    public void clear() {
        map = new HashMap<Long, PlayerOnline>();
        list = new ArrayList<PlayerOnline>();
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
    /* Can consider making non-parametric UserList
     * to get better error info. */
    public boolean add(long uid, PlayerOnline player) {
        if(!contains(uid)){
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
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(long uid) {
        return map.get(uid) != null;
    }

    @Override
    public Iterator<PlayerOnline> iterator() {
        return list.iterator();
    }

    @Override
    public Iterator<String> usernameIterator() {
        return null;
    }
}
