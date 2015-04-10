package objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import engine.AssetManager;
import engine.UserInputProcessor;

public class PlayerAnimation {

    private Player player;
    private Animation currentAnim;
    private float currentDelta;

    public PlayerAnimation(Player p, PlayerType t) {
        player = p;
        currentAnim = AssetManager.getAnimation(t, State.IDLE, Direction.DOWN);
    }

    /**
     * Ensures new animations start from first frame.
     */
    public void updateAnim() {
        if (player.isNewState) {
            player.isNewState = false;
            currentDelta = 0;
        }
        currentAnim = AssetManager.getAnimation(player.getType(), player.getState(), player.getDirection());
    }

    public TextureRegion getFrame(float delta) {
        updateAnimLogic(delta);
        return currentAnim.getKeyFrame(currentDelta);
    }

    /**
     * Handles logic for rendering animations.
     * Note: Will ignore player input until ATTACK animations complete.
     */
    private void updateAnimLogic(float delta) {
        currentDelta += delta;
        if(currentAnim.isAnimationFinished(currentDelta)) {
            currentDelta -= currentAnim.getAnimationDuration();

            switch (player.getState()) {
                case ATK_MELEE:
                    UserInputProcessor.attackKeys[0] = false;
                    break;
                case ATK_RANGED:
                    UserInputProcessor.attackKeys[1] = false;
                    break;
            }
        }
    }
}
