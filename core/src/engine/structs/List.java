package engine.structs;

import java.util.Iterator;

public interface List<E> extends Iterable<E> {

    public E remove(int i);
    public E remove(long uid);
    public void clear();
    public E get(int i);
    public E get(long uid);
    /**
     * Attempts to add user to list. Will print an error and return false if
     * user already exists.
     * @return - true if successful, false otherwise
     */
    public boolean add(long uid, E e);
    public int size();
    public boolean isEmpty();
    public boolean contains(long uid);
    public Iterator<E> iterator();
    public Iterator<String> usernameIterator();

}
