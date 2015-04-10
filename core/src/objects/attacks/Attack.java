package objects.attacks;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import objects.Direction;
import objects.GameObject;
import objects.Player;
import objects.PlayerType;

public abstract class Attack extends GameObject {

    protected long id, uid;
    protected PlayerType ptype;
    protected AttackType type;
    protected Animation anim;
    protected boolean isAlive;
    protected int width, height;
    protected float currentDelta, offsetX, offsetY;

    public Attack(long id, long uid, PlayerType ptype, Direction d, float x, float y, int w, int h, AttackType type, boolean isAlive) {
        super(d, x, y);
        this.id = id;
        this.uid = uid;
        this.ptype = ptype;
        this.type = type;
        this.width = w;
        this.height = h;
        this.isAlive = isAlive;
        this.currentDelta = 0;
    }

    /**
     * Returns true if collision is detected with main player, false otherwise.
     */
    public abstract boolean update(Player mp);

    public abstract void render(float delta, SpriteBatch batch);

    protected abstract void updateLogic(Player mp);

    protected abstract void updateAnim();

    protected abstract void updateDamage(Player mp);

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
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

    public AttackType getAtkType() { return type; }

    public boolean isAlive() {
        return isAlive;
    }

    public int isAliveNum() {
        return (isAlive)?1:0;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public AttackType getType() {
        return type;
    }
}
