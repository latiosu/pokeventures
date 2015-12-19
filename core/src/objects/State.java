package objects;

public enum State {
    IDLE(0),
    WALK(1),
    ATK_RANGED(3);

    private int num;

    State(int num) {
        this.num = num;
    }

    public static State getState(int i) {
        switch (i) {
            case 0:
                return IDLE;
            case 1:
                return WALK;
            case 3:
                return ATK_RANGED;
            default:
                System.err.println("Error: State not found.");
                return null;
        }
    }

    public int getNum() {
        return num;
    }
}
