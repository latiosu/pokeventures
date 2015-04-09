package objects.attacks;

public enum AttackType {
    NONE(0),
    MELEE(1),
    PROJECTILE(2);

    private int num;
    AttackType(int num) {
        this.num = num;
    }
    public int getNum(){
        return num;
    }
    public static AttackType getType(int i) {
        switch (i) {
            case 0:
                return NONE;
            case 1:
                return MELEE;
            case 2:
                return PROJECTILE;
            default:
                System.err.println("Error: Attack not found.");
                return null;
        }
    }
}
