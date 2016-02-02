package engine.structs;

import java.util.Comparator;

public class TimeComparator implements Comparator<Message> {
    @Override
    public int compare(Message a, Message b) {
        if (a.time == b.time) {
            return 0;
        } else if (a.time > b.time) {
            return 1;
        } else {
            return -1;
        }
    }
}
