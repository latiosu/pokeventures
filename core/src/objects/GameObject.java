package objects;

import objects.structs.Direction;

public abstract class GameObject {

    protected float x, y;
    protected Direction direction;
    protected boolean isAlive;

    protected GameObject() {
        this(Direction.DOWN, 0, 0);
    }

    protected GameObject(Direction d, float x, float y) {
        this.direction = d;
        this.x = x;
        this.y = y;
    }

    // Getters and Setters
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

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int isAliveNum() {
        return (isAlive) ? 1 : 0;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

}
