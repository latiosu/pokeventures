package objects;

public enum PlayerType {
    CHARMANDER("charmander", 1),
    BULBASAUR("bulbasaur", 2),
    SQUIRTLE("squirtle", 3);

    private int num;
    private String name;

    PlayerType(String name, int num) {
        this.name = name;
        this.num = num;
    }

    public static PlayerType getType(int i) {
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

    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }
}
