package objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import engine.AssetManager;

public class PlayerAnimation {

    private Player player;
    private Animation currentAnimation;
    private float pauseDelta, lastDelta;

    public PlayerAnimation(Player p, Player.Type t) {
        player = p;
        pauseDelta = 0;
        lastDelta = 0;
        currentAnimation = AssetManager.getAnimation(t, Entity.Direction.DOWN);
    }

    public void changeAnim(){
        currentAnimation = AssetManager.getAnimation(player.getType(), player.getDirection());
    }

    public TextureRegion getFrame(float delta){
        if(player.isMoving) {
            lastDelta = delta;
            return currentAnimation.getKeyFrame(delta);
        } else {
            pauseDelta = lastDelta;
            return currentAnimation.getKeyFrame(pauseDelta);
        }
    }
}
