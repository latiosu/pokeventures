package engine.structs;

import java.util.Iterator;

public interface List<E> extends Iterable<E> {

    E remove(int i);

    E remove(long uid);

    E get(int i);

    E get(long uid);

    /**
     * Attempts to add user to list. Will print an error and return false if
     * user already exists.
     *
     * @return - true if successful, false otherwise
     */
    boolean add(long uid, E e);

    int size();

    boolean contains(long uid);

    Iterator<E> iterator();
}
