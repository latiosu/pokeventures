package objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import engine.AssetManager;
import engine.Config;

import java.util.Date;

public class Attack extends GameObject {

    protected long id;
    protected Player owner;
    protected Animation anim;
    protected boolean isAlive;
    protected int width, height;
    protected float currentDelta, offsetX, offsetY;

    private final float MAX_RANGE = 85;
    private final float TRAVEL_RATE = 10;
    private final float OFFSET = 4;
    private final float DAMAGE = 12.5f;

    public Attack(long id, Player p, Direction dir, float x, float y) {
        super(p.getDirection(), p.getX(), p.getY());
        this.id = id;
        this.owner = p;
        this.direction = dir;
        this.x = x;
        this.y = y;
        this.width = Config.TILE_SIZE;
        this.height = Config.TILE_SIZE;
        this.isAlive = true;
        this.currentDelta = 0;
        anim = AssetManager.getAnimation(owner.getType(), State.ATK_RANGED, direction, true);

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
    public Attack(Player mp) {
        this(new Date().getTime(), mp, mp.getDirection(), mp.getX(), mp.getY());
    }

    /**
     * Returns true if collision is detected with main player, false otherwise.
     */
    public boolean update(Player mp) {
        // Skip update if projectile is not alive
        if (!isAlive()) {
            return false;
        }

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
            return false;
        } else if (mp.getBounds().overlaps(this.getBounds()) && mp.getUID() != owner.getUID() && mp.isAlive()) {
            // Collision resolution
            if (Config.DEBUG) {
                System.out.printf("You (%s) have been hit for %s (%s/%s)\n",
                        mp.getUsername(), DAMAGE, mp.getHp(), mp.getMaxHp());
            }
            mp.updateDamage(this);
            setAlive(false);
            return true;
        }
        return false;
    }

    public void render(float delta, SpriteBatch batch) {
        currentDelta += delta;
        if (isAlive()) {
            batch.draw(anim.getKeyFrame(currentDelta), getPosX(), getPosY());
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

    public Player getOwner() {
        return owner;
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
