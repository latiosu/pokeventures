package objects.structs;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import engine.AssetManager;
import objects.Player;
import objects.structs.Direction;
import objects.structs.PlayerType;
import objects.structs.State;

public class PlayerAnimation {

    private Player player;
    private Animation currentAnim;
    private float currentDelta;

    public PlayerAnimation(Player p, PlayerType t) {
        player = p;
        currentAnim = AssetManager.getAnimation(t, State.IDLE, Direction.DOWN, false);
    }

    /**
     * Ensures new animations start from first frame.
     */
    public void updateAnim() {
        if (player.isNewState()) {
            player.setNewState(false);
            currentDelta = 0;
        }
        currentAnim = AssetManager.getAnimation(player.getType(), player.getState(), player.getDirection(), false);
    }

    public TextureRegion getFrame(float delta) {
        currentDelta += delta;
        if (currentAnim.isAnimationFinished(currentDelta)) {
            currentDelta -= currentAnim.getAnimationDuration();
        }
        return currentAnim.getKeyFrame(currentDelta);
    }
}
