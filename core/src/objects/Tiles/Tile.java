package objects.Tiles;

import com.badlogic.gdx.math.Rectangle;
import engine.Config;
import objects.Entity;

/* Collision Handling Entity */
public abstract class Tile {

    protected float x, y;
    protected int tileSize;

    public static enum Type {
        WALKABLE,
        WALKABLE_HORIZONTAL,
        WALKABLE_VERTICAL,
        BLOCKED,
    }

    public Tile(float x, float y) {
        this.x = x;
        this.y = y;
        this.tileSize = Config.TILE_SIZE;
    }

    public abstract float handleCollision(float px, float py, Entity.Direction dir);
//    public abstract float handleCollision(Rectangle r, Entity.Direction dir);

    private float toPlayerX(float x) {
        return x;
    }
    private float toPlayerY(float y) {
        return y;
    }
    protected float getTop() {
        return toPlayerY(y)-1;
    }
    protected float getBottom() {
        return toPlayerY(y + tileSize)+1;
    }
    protected float getRight() {
        return toPlayerX(x + tileSize)+1;
    }
    protected float getLeft() {
        return toPlayerX(x)-1;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
}
