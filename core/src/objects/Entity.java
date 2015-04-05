package objects;

public abstract class Entity {

    protected Player.Type type;
    protected Direction direction;
    protected boolean isMoving;
    protected float x, y;

    public static enum Direction {
        DOWN(1),
        LEFT(2),
        UP(3),
        RIGHT(4);

        private int num;
        Direction(int num) {
            this.num = num;
        }
        public int getNum(){
            return num;
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
                    return null;
            }
        }
    }
    public static enum Type {
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

    public Entity(Player.Type type, Direction d) {
        this.type = type;
        this.direction = d;
        this.isMoving = false;
        this.x = 0;
        this.y = 0;
    }

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
    public boolean isMoving() {
        return isMoving;
    }
    public void setMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }
    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction d) {
        this.direction = d;
    }
    public Player.Type getType() {
        return type;
    }
    public void setType(Player.Type type) {
        this.type = type;
    }
}
