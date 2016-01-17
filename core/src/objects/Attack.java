package objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import engine.AssetManager;
import engine.Config;
import engine.structs.AttackList;
import networking.packets.Packet04Attack;

import java.util.Date;

public class Attack extends GameObject {

    protected long id, uid;
    protected PlayerType ptype;
    protected Animation anim;
    protected boolean isAlive;
    protected int width, height;
    protected float currentDelta, offsetX, offsetY;

    private final float MAX_RANGE = 85;
    private final float TRAVEL_RATE = 10;
    private final float OFFSET = 4;
    private final float DAMAGE = 12.5f;

    public Attack(long id, Player p) {
        super(p.getDirection(), p.getX(), p.getY());
        this.id = id;
        this.uid = p.getUID();
        this.ptype = p.getType();
        this.width = Config.TILE_SIZE;
        this.height = Config.TILE_SIZE;
        this.isAlive = true;
        this.currentDelta = 0;
        anim = AssetManager.getAnimation(ptype, State.ATK_RANGED, direction, true);
        updatePosition(this.direction, OFFSET);
    }

    public Attack(Player mp) {
        this(new Date().getTime(), mp);
    }

    /**
     * Returns true if collision is detected with main player, false otherwise.
     */
    public boolean update(Player mp) {
        updateLogic(mp);
        updateAnim();
        if (mp.getBounds().overlaps(this.getBounds()) && mp.getUID() != this.getUid()) {
            updateDamage(mp);
            isAlive = false;
            return true;
        }
        return false;
    }

    public void render(float delta, SpriteBatch batch) {
        currentDelta += delta;
        if (isAlive) {
            batch.draw(anim.getKeyFrame(currentDelta), x + offsetX, y + offsetY);
        }
    }

    protected void updateLogic(Player mp) {
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
        if (Math.abs(offsetX) > MAX_RANGE || Math.abs(offsetY) > MAX_RANGE) {
            isAlive = false;
        }
    }

    protected void updateAnim() {

    }

    protected void updateDamage(Player mp) {
        System.out.printf("%s has been hit for %s\n", mp.getUsername(), DAMAGE);
    }

    protected void updatePosition(Direction direction, float offset) {
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
     * Computes whether enough time has passed since the last attack.
     * Attack speed differs between player types and can be set within Config class.
     * @param mp - target player to check
     * @return true if player can attack, false otherwise
     */
    public static boolean canRangedAttack(Player mp) {
        double attackDeltaTime = (System.nanoTime() - mp.getLastAttackTime()) / 10e8;
        System.out.println(attackDeltaTime);
        switch(mp.getType()) {
            case CHARMANDER:
                if (attackDeltaTime > 1.0/ Config.MAX_CHARMANDER_ASPD) {
                    return true;
                }
                break;
            case BULBASAUR:
                if (attackDeltaTime > 1.0/Config.MAX_BULBASAUR_ASPD) {
                    return true;
                }
                break;
            case SQUIRTLE:
                if (attackDeltaTime > 1.0/Config.MAX_SQUIRTLE_ASPD) {
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

    public long getUid() {
        return uid;
    }

    public PlayerType getPtype() {
        return ptype;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int isAliveNum() {
        return (isAlive)?1:0;
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


}
