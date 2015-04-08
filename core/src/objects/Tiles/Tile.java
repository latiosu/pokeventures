package objects.Tiles;

import com.badlogic.gdx.math.Rectangle;
import engine.Config;
import objects.PlayerOnline;

/* Collision Handling Entity */
public abstract class Tile {

    protected float x, y;
    protected int tileSize;
    protected Rectangle bounds;

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
        this.bounds = new Rectangle(x, y, tileSize, tileSize);
    }

    public abstract void handleCollision(PlayerOnline mp);

    public Rectangle getBounds() {
        return bounds;
    }
    protected float getTop() {
        return (y + tileSize);
    }
    protected float getBottom() {
        return (y - Config.CHAR_COLL_HEIGHT);
    }
    protected float getRight() {
        return (x + tileSize);
    }
    protected float getLeft() {
        return (x) - Config.CHAR_COLL_WIDTH;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
}
