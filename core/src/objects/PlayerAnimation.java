package objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import engine.AssetManager;

public class PlayerAnimation {

    private Player player;
    private Animation currentAnim;
    private float pauseDelta, lastDelta;

    public PlayerAnimation(Player p, PlayerType t) {
        player = p;
        pauseDelta = 0;
        lastDelta = 0;
        currentAnim = AssetManager.getAnimation(t, State.IDLE, Direction.DOWN);
    }

    public void updateAnim() {
        currentAnim = AssetManager.getAnimation(player.getType(), player.getState(), player.getDirection());
    }

    public TextureRegion getFrame(float delta) {
        if (player.getState() != State.IDLE) {
            lastDelta = delta;
            return currentAnim.getKeyFrame(delta);
        } else {
            pauseDelta = lastDelta;
            return currentAnim.getKeyFrame(pauseDelta);
        }
    }
}
