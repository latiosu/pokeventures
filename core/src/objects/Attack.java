package objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import engine.AssetManager;
import objects.structs.Direction;
import objects.structs.State;

import java.util.Date;

public class Attack extends BaseAttack {

    protected Animation anim;
    protected float currentDelta;

    public Attack(long id, Player p, Direction dir, float x, float y) {
        super(id, p, dir, x, y);
        anim = AssetManager.getAnimation(owner.getType(), State.ATK_RANGED, direction, true);
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

    public void render(float delta, SpriteBatch batch) {
        currentDelta += delta;
        if (isAlive()) {
            batch.draw(anim.getKeyFrame(currentDelta), getPosX(), getPosY());
        }
    }
}
