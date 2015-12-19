package objects.attacks;

public enum AttackType {
    NONE(0),
    RANGED(2);

    private int num;

    AttackType(int num) {
        this.num = num;
    }

    public static AttackType getType(int i) {
        switch (i) {
            case 0:
                return NONE;
            case 2:
                return RANGED;
            default:
                System.err.println("Error: Attack not found.");
                return null;
        }
    }

    public int getNum() {
        return num;
    }
}
