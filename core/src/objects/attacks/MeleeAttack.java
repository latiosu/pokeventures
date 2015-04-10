package objects.attacks;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import engine.AssetManager;
import engine.Config;
import objects.Direction;
import objects.Player;
import objects.PlayerType;
import objects.State;

import java.util.Date;

public class MeleeAttack extends Attack {

    private final float MAX_RANGE = 1;
    private final float OFFSET = 4;
    private Animation anim;

    public MeleeAttack(long id, long uid, PlayerType ptype, Direction dir, float x, float y) {
        super(id, uid, ptype, dir, x, y, Config.TILE_SIZE, Config.TILE_SIZE,  AttackType.MELEE, true);
        anim = AssetManager.getAnimation(ptype, State.ATK_MELEE, direction, true);
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

    public MeleeAttack(long uid, PlayerType ptype, Direction dir, float x, float y) {
        this(new Date().getTime(), uid, ptype, dir, x, y);
    }

    @Override
    public boolean update(Player mp) {
        updateLogic(mp);
        updateAnim();
        if (mp.getBounds().overlaps(getBounds())) {
            updateDamage(mp);
            return true;
        }
        return false;
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        currentDelta += delta;
        if (isAlive) {
            batch.draw(anim.getKeyFrame(currentDelta), x + offsetX, y + offsetY);
        }
    }

    @Override
    protected void updateLogic(Player mp) {
        if (anim.isAnimationFinished(currentDelta)) {
            isAlive = false;
        }
    }

    @Override
    protected void updateAnim() {

    }

    @Override
    protected void updateDamage(Player mp) {

    }
}
