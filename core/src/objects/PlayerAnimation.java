package objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import engine.AssetManager;

import java.util.Map;

public class PlayerAnimation {

    private AssetManager assets;
    private Player player;
    private Animation currentAnimation;
    private boolean playing = false;

    public PlayerAnimation(Player p, Type t, AssetManager assets) {
        player = p;
        this.assets = assets;
        currentAnimation = assets.getAnimation(t, Direction.DOWN);
    }

    public void play(){
        playing = true;
        currentAnimation = assets.getAnimation(player.getType(), player.getDirection());
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
