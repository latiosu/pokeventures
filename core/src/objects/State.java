package objects;

public enum State {
    IDLE(0),
    WALK(1),
    ATK_MELEE(2),
    ATK_RANGED(3);

    private int num;
    State(int num) {
        this.num = num;
    }
    public int getNum(){
        return num;
    }
    public static State getState(int i) {
        switch (i) {
            case 0:
                return IDLE;
            case 1:
                return WALK;
            case 2:
                return ATK_MELEE;
            case 3:
                return ATK_RANGED;
            default:
                System.err.println("Error: State not found.");
                return null;
        }
    }
}
