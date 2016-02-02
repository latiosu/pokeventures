package objects.structs;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import engine.AssetManager;
import engine.Logger;
import objects.Player;

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

            // Remove Jump state upon animation completing
            if (player.getState() == State.JUMP) {
                player.setState(State.IDLE);
            }
        }
        return currentAnim.getKeyFrame(currentDelta);
    }


    public void triggerJump(Direction dir) {
        switch (dir) {
            case DOWN:
                player.setDirection(Direction.DOWN);
                break;
            case LEFT:
                player.setDirection(Direction.LEFT);
                break;
            default:
                Logger.log(Logger.Level.ERROR,
                        "No animation has been created for this jump direction (%s)\n",
                        dir);
                break;
        }
        player.setState(State.JUMP);
        updateAnim();
    }
}
