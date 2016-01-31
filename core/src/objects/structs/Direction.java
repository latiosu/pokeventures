package objects.structs;

import engine.Logger;

public enum Direction {
    DOWN(1),
    LEFT(2),
    UP(3),
    RIGHT(4);

    private int num;

    Direction(int num) {
        this.num = num;
    }

    public static Direction getDir(int i) {
        switch (i) {
            case 1:
                return DOWN;
            case 2:
                return LEFT;
            case 3:
                return UP;
            case 4:
                return RIGHT;
            default:
                Logger.log(Logger.Level.ERROR, "Direction not found\n");
                return null;
        }
    }

    public int getNum() {
        return num;
    }
}
