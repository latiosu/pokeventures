package engine.structs;

import objects.attacks.Attack;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AttackList implements List<Attack> {

    private Map<Long, Attack> map;
    private java.util.List<Attack> list;

    public AttackList() {
        map = new ConcurrentHashMap<Long, Attack>();
        list = new CopyOnWriteArrayList<Attack>(); /* <-- Works well for small array lists */
    }

    @Override
    public Attack remove(int i) {
        return map.remove(list.remove(i));
    }

    @Override
    public Attack remove(long uid) {
        Attack result = get(uid);
        list.remove(result);
        return result;
    }

    @Override
    public Attack get(int i) {
        return list.get(i);
    }

    @Override
    public Attack get(long id) {
        return map.get(id);
    }

    @Override
    public boolean add(long id, Attack atk) {
        if (!contains(id)) {
            map.put(id, atk);
            list.add(atk);
        } else {
            System.err.println("Error: Attack already exists - " + atk.getId());
            return false;
        }
        return true;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean contains(long id) {
        return map.get(id) != null;
    }

    @Override
    public Iterator<Attack> iterator() {
        return list.iterator();
    }
}
