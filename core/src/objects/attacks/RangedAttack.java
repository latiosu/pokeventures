package objects.attacks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import engine.AssetManager;
import engine.Config;
import objects.Direction;
import objects.Player;
import objects.PlayerType;
import objects.State;

import java.util.Date;

public class RangedAttack extends Attack {

    private final float MAX_RANGE = 85;
    private final float TRAVEL_RATE = 10;
    private final float OFFSET = 4;

    public RangedAttack(long id, long uid, PlayerType ptype, Direction dir, float x, float y) {
        super(id, uid, ptype, dir, x, y, Config.TILE_SIZE, Config.TILE_SIZE,  AttackType.RANGED, true);
        anim = AssetManager.getAnimation(ptype, State.ATK_RANGED, direction, true);
        updatePosition(dir, OFFSET);
    }

    public RangedAttack(long uid, PlayerType ptype, Direction dir, float x, float y) {
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
            mp.setHasRangedAtk(false);
        }
    }

    @Override
    protected void updateAnim() {

    }

    @Override
    protected void updateDamage(Player mp) {

    }
}
