package engine.structs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UserList<E> implements List<E> {

    private Map<String, E> map;
    private java.util.List<E> list;

    public UserList() {
        map = new HashMap<String, E>();
        list = new ArrayList<E>();
    }

    @Override
    public E remove(int i) {
        return map.remove(list.remove(i));
    }

    @Override
    public E remove(String s) {
        E result = get(s);
        list.remove(result);
        return result;
    }

    @Override
    public void clear() {
        map = new HashMap<String, E>();
        list = new ArrayList<E>();
    }

    @Override
    public E get(int i) {
        return list.get(i);
    }

    @Override
    public E get(String s) {
        return map.get(s);
    }

    @Override
    public boolean add(String s, E e) {
        if(!contains(s)){
            map.put(s, e);
            list.add(e);
        } else {
            System.err.print("Error: User already exists");
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
    public boolean contains(String s) {
        return map.get(s) != null;
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public Iterator<String> usernameIterator() {
        return null;
    }
}
