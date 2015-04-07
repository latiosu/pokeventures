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
    private float toPlayerX(float x) {
        return x;
    }
    private float toPlayerY(float y) {
        return y;
    }
    protected float getTop() {
        return toPlayerY(y + tileSize);
    }
    protected float getBottom() {
        return toPlayerY(y - tileSize);
    }
    protected float getRight() {
        return toPlayerX(x + tileSize);
    }
    protected float getLeft() {
        return toPlayerX(x) - Config.CHAR_WIDTH;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
}
