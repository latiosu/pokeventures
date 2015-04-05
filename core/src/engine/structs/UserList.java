package engine.structs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserList<Player> implements List<Player> {

    private Map<Long, Player> map;
    private java.util.List<Player> list;
    private Player mainPlayer;

    public UserList() {
        map = new HashMap<Long, Player>();
        list = new CopyOnWriteArrayList<Player>(); /* <-- Works well for small array lists */
    }

    public Player getMainPlayer() {
        if(mainPlayer == null){
            System.err.println("Error: Main player has not been set.");
        }
        return mainPlayer;
    }

    public void setMainPlayer(Player p) {
        mainPlayer = p;
    }

    @Override
    public Player remove(int i) {
        return map.remove(list.remove(i));
    }

    @Override
    public Player remove(long uid) {
        Player result = get(uid);
        list.remove(result);
        return result;
    }

    @Override
    public void clear() {
        map = new HashMap<Long, Player>();
        list = new ArrayList<Player>();
    }

    @Override
    public Player get(int i) {
        return list.get(i);
    }

    @Override
    public Player get(long uid) {
        return map.get(uid);
    }

    @Override
    /* Can consider making non-parametric UserList
     * to get better error info. */
    public boolean add(long uid, Player player) {
        if(!contains(uid)){
            map.put(uid, player);
            list.add(player);
        } else {
            System.err.println("Error: User already exists - " + uid);
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
    public Iterator<Player> iterator() {
        return list.iterator();
    }

    @Override
    public Iterator<String> usernameIterator() {
        return null;
    }
}
