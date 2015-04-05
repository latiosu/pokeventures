package objects;

public enum Type {
    CHARMANDER(1),
    BULBASAUR(2),
    SQUIRTLE(3);

    private int num;
    Type(int num) {
        this.num = num;
    }
    public int getNum(){
        return num;
    }
    public static Type getType(int i) {
        switch (i) {
            case 1:
                return CHARMANDER;
            case 2:
                return BULBASAUR;
            case 3:
                return SQUIRTLE;
            default:
                return null;
        }
    }
}
