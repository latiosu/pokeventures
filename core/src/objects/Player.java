package objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import engine.AssetManager;

public class Player extends Entity {

    private PlayerAnimation anim;
    public boolean isMain;

    public Player(AssetManager assets) {
        this(Type.CHARMANDER, assets, true);
    }

    public Player(Type type, AssetManager assets, boolean isMain){
        super(type, Direction.DOWN, assets);
        anim = new PlayerAnimation(this, type, assets);
        this.isMain = isMain;
    }

    public void move(Direction d){
        this.setMoving(true);
        this.setDirection(d);
    }

    public TextureRegion getFrame(float delta){
        return anim.getFrame(delta);
    }

    public PlayerAnimation getAnim() {
        return anim;
    }
}
