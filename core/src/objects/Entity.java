package objects;

public abstract class Entity extends GameObject {

    protected PlayerType type;
    protected boolean isMoving;

    protected Entity(PlayerType type) {
        super();
        this.type = type;
        this.isMoving = false;
    }

    public boolean isMoving() {
        return isMoving;
    }
    public void setMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }
    public PlayerType getType() {
        return type;
    }
    public void setType(PlayerType type) {
        this.type = type;
    }
}
