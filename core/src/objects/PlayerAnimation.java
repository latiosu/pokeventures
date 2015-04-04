package objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import engine.AssetManager;

import java.util.Map;

public class PlayerAnimation {

    private Player player;
    private Animation currentAnimation;
    private boolean playing = false;

    public PlayerAnimation(Player p, Type t) {
        player = p;
        currentAnimation = AssetManager.getAnimation(t, Direction.DOWN);
    }

    public void play(){
        playing = true;
        currentAnimation = AssetManager.getAnimation(player.getType(), player.getDirection());
    }

    public void stop(){
        playing = false;
    }

    public TextureRegion getFrame(float delta){
        if(playing && !currentAnimation.isAnimationFinished(delta)){
            return currentAnimation.getKeyFrame(delta,false);
        } else {
            stop();
            return currentAnimation.getKeyFrame(0);
        }
    }
}
