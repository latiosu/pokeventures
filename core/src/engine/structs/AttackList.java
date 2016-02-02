package engine.structs;

import engine.Logger;
import objects.BaseAttack;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AttackList implements List<BaseAttack> {

    private Map<Long, BaseAttack> map;
    private java.util.List<BaseAttack> list;

    public AttackList() {
        map = new ConcurrentHashMap<>();
        list = new CopyOnWriteArrayList<>(); /* <-- Works well for small array lists */
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public BaseAttack remove(int i) {
        return map.remove(list.remove(i));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public BaseAttack remove(long uid) {
        BaseAttack result = get(uid);
        map.remove(list.remove(result));
        return result;
    }

    @Override
    public BaseAttack get(int i) {
        return list.get(i);
    }

    @Override
    public BaseAttack get(long id) {
        return map.get(id);
    }

    @Override
    public boolean add(long id, BaseAttack atk) {
        if (!contains(id)) {
            map.put(id, atk);
            list.add(atk);
        } else {
            Logger.log(Logger.Level.ERROR,
                    "Attack already exists - %s\n",
                    atk.getId());
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
    public Iterator<BaseAttack> iterator() {
        return list.iterator();
    }
}
