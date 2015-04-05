package engine.structs;

import java.util.Iterator;

public interface List<E> extends Iterable<E> {

    public E remove(int i);
    public E remove(String s);
    public void clear();
    public E get(int i);
    public E get(String s);
    /**
     * Attempts to add user to list. Will print an error and return false if
     * user already exists.
     * @return - true if successful, false otherwise
     */
    public boolean add(String s, E e);
    public int size();
    public boolean isEmpty();
    public boolean contains(String s);
    public Iterator<E> iterator();
    public Iterator<String> usernameIterator();

}
