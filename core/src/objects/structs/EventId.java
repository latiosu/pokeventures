package objects.structs;

import engine.Logger;

public enum EventId {
    LEVEL_UP(0),
    HEALTH_PICKUP(1);

    private int num;

    EventId(int num) {
        this.num = num;
    }

    public static EventId getEvent(int i) {
        switch (i) {
            case 0:
                return LEVEL_UP;
            case 1:
                return HEALTH_PICKUP;
            default:
                Logger.log(Logger.Level.ERROR,
                        "EventId number not found (%s)\n",
                        i);
                return null;
        }
    }

    public int getNum() {
        return num;
    }
}
