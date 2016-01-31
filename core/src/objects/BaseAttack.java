package objects;

import com.badlogic.gdx.math.Rectangle;
import engine.Config;
import engine.Logger;
import objects.structs.Direction;

import java.util.Date;

public class BaseAttack extends GameObject {

    protected long id;
    protected BasePlayer owner;
    protected int width, height;
    protected float currentDelta, offsetX, offsetY;

    protected final float MAX_RANGE = 85;
    protected final float TRAVEL_RATE = 10;
    protected final float OFFSET = 4;
    protected final float DAMAGE = 12.5f;

    public BaseAttack(long id, BasePlayer p, Direction dir, float x, float y) {
        super(dir, x, y);
        this.id = id;
        this.owner = p;
        this.width = Config.TILE_SIZE;
        this.height = Config.TILE_SIZE;
        this.isAlive = true;
        this.currentDelta = 0;

        // Offset projectile to center on player
        switch (direction) {
            case DOWN:
                offsetY = -OFFSET;
                break;
            case LEFT:
                offsetX = -OFFSET;
                break;
            case UP:
                offsetY = OFFSET;
                break;
            case RIGHT:
                offsetX = OFFSET;
                break;
        }
    }

    /**
     * Convenience constructor for generating new attacks from given player. Will use the current time as UID,
     * player direction and position as parameters.
     *
     * @param mp - player generating attack
     */
    public BaseAttack(Player mp) {
        this(new Date().getTime(), mp, mp.getDirection(), mp.getX(), mp.getY());
    }

    /**
     * Returns true if collision is detected with player, false otherwise.
     */
    public boolean checkForCollision(BasePlayer p) {
        // Skip update if projectile is not alive
        if (!isAlive()) {
            return false;
        } else if (p.getBounds().overlaps(this.getBounds()) && p.getUid() != owner.getUid() && p.isAlive()) {
            // Collision resolution
            p.updateDamage(this);
            setAlive(false);
            return true;
        }
        return false;
    }

    /**
     * Updates attack to it's next position. Will be marked as dead if exceeds maximum range.
     */
    public void updatePosition() {
        // Position updates
        switch (direction) {
            case DOWN:
                offsetY -= TRAVEL_RATE;
                break;
            case LEFT:
                offsetX -= TRAVEL_RATE;
                break;
            case UP:
                offsetY += TRAVEL_RATE;
                break;
            case RIGHT:
                offsetX += TRAVEL_RATE;
                break;
        }

        // Kill projectile if out of range
        if (Math.abs(offsetX) > MAX_RANGE || Math.abs(offsetY) > MAX_RANGE) {
            setAlive(false);
        }
    }

    /**
     * Computes whether enough time has passed since the last attack.
     * Attack speed differs between player types and can be set within Config class.
     *
     * @param mp - target player to check
     * @return true if player can attack, false otherwise
     */
    public static boolean canRangedAttack(Player mp) {
        double attackDeltaTime = (System.nanoTime() - mp.getLastAttackTime()) / 1e9;
        switch (mp.getType()) {
            // TODO: Make ASPD + TRAVEL_RATE local to class and change based on player type
            case CHARMANDER:
                if (attackDeltaTime > 1.0 / Config.MAX_CHARMANDER_ASPD) {
                    return true;
                }
                break;
            case BULBASAUR:
                if (attackDeltaTime > 1.0 / Config.MAX_BULBASAUR_ASPD) {
                    return true;
                }
                break;
            case SQUIRTLE:
                if (attackDeltaTime > 1.0 / Config.MAX_SQUIRTLE_ASPD) {
                    return true;
                }
                break;
        }
        return false;
    }

    public Rectangle getBounds() {
        return new Rectangle(getPosX(), getPosY(), width, height);
    }

    public long getId() {
        return id;
    }

    public BasePlayer getOwner() {
        return owner;
    }

    public float getPosX() {
        return x + offsetX;
    }

    public float getPosY() {
        return y + offsetY;
    }

    public float getDamage() {
        return DAMAGE;
    }

}
