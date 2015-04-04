package objects;

import engine.AssetManager;

public abstract class Entity {

    protected AssetManager assets;
    protected Type type;
    protected Direction direction;
    protected boolean isMoving;
    protected float x, y;

    public Entity(Type type, Direction d, AssetManager assets) {
        this.assets = assets;
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
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
}
