package objects;

import com.badlogic.gdx.math.Rectangle;
import engine.Config;
import objects.structs.Direction;

import java.util.Date;

public class BaseAttack extends GameObject {

    protected long id;
    protected BasePlayer owner;
    protected int width, height;
    protected float currentDelta, offsetX, offsetY;

    protected float maxRange = 85;
    protected float moveSpeed = 10;
    protected float offset = 4;
    protected float damage = 12.5f;
    protected double maxAspd;

    public BaseAttack(long id, BasePlayer p, Direction dir, float x, float y) {
        super(dir, x, y);
        this.id = id;
        this.owner = p;
        this.width = Config.TILE_SIZE;
        this.height = Config.TILE_SIZE;
        this.isAlive = true;
        this.currentDelta = 0;

        // Adjust attack base values baed on player type
        switch (p.getType()) {
            case CHARMANDER:
                maxRange = Config.CHARMANDER_ATKRANGE;
                moveSpeed = Config.CHARMANDER_ATKMSPD;
                offset = Config.CHARMANDER_ATKOFFSET;
                damage = Config.CHARMANDER_ATKDMG;
                maxAspd = Config.CHARMANDER_MAXASPD;
                break;
            case BULBASAUR:
                maxRange = Config.BULBASAUR_ATKRANGE;
                moveSpeed = Config.BULBASAUR_ATKMSPD;
                offset = Config.BULBASAUR_ATKOFFSET;
                damage = Config.BULBASAUR_ATKDMG;
                maxAspd = Config.BULBASAUR_MAXASPD;
                break;
            case SQUIRTLE:
                maxRange = Config.SQUIRTLE_ATKRANGE;
                moveSpeed = Config.SQUIRTLE_ATKMSPD;
                offset = Config.SQUIRTLE_ATKOFFSET;
                damage = Config.SQUIRTLE_ATKDMG;
                maxAspd = Config.SQUIRTLE_MAXASPD;
                break;
        }

        // Offset projectile to center on player
        switch (direction) {
            case DOWN:
                offsetY = -offset;
                break;
            case LEFT:
                offsetX = -offset;
                break;
            case UP:
                offsetY = offset;
                break;
            case RIGHT:
                offsetX = offset;
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
                offsetY -= moveSpeed;
                break;
            case LEFT:
                offsetX -= moveSpeed;
                break;
            case UP:
                offsetY += moveSpeed;
                break;
            case RIGHT:
                offsetX += moveSpeed;
                break;
        }

        // Kill projectile if out of range
        if (Math.abs(offsetX) > maxRange || Math.abs(offsetY) > maxRange) {
            setAlive(false);
        }
    }

    /**
     * Computes whether enough time has passed since the last attack.
     * Attack speed differs between player types and can be set within Config class.
     *
     * @param p - target player to check
     * @return true if player can attack, false otherwise
     */
    public static boolean canRangedAttack(Player p) {
        double attackDeltaTime = (System.nanoTime() - p.getLastAttackTime()) / 1e9;
        switch (p.getType()) {
            case CHARMANDER:
                return attackDeltaTime > 1.0 / Config.CHARMANDER_MAXASPD;
            case BULBASAUR:
                return attackDeltaTime > 1.0 / Config.BULBASAUR_MAXASPD;
            case SQUIRTLE:
                return attackDeltaTime > 1.0 / Config.SQUIRTLE_MAXASPD ;
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
        return damage;
    }

}
