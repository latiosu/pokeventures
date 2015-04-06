package engine.structs;

import objects.PlayerOnline;

import java.util.Iterator;

public interface List extends Iterable<PlayerOnline> {

    public PlayerOnline remove(int i);
    public PlayerOnline remove(long uid);
    public void clear();
    public PlayerOnline get(int i);
    public PlayerOnline get(long uid);
    /**
     * Attempts to add user to list. Will print an error and return false if
     * user already exists.
     * @return - true if successful, false otherwise
     */
    public boolean add(long uid, PlayerOnline e);
    public int size();
    public boolean isEmpty();
    public boolean contains(long uid);
    public Iterator<PlayerOnline> iterator();
    public Iterator<String> usernameIterator();

}
