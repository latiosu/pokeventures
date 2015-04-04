package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import objects.Direction;
import objects.Type;

import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    private final float ANIM_RATE = 1/5f;
    private Map<Type, Map<Direction, Animation>> animations;

    public AssetManager() {
        animations = new HashMap<Type, Map<Direction, Animation>>();
        loadAssets();
    }

    private void loadAssets() {
        // Load character animations
        animations.put(Type.CHARMANDER, generate(new TextureAtlas(Gdx.files.internal("charmander.atlas"))));
        animations.put(Type.BULBASAUR, generate(new TextureAtlas(Gdx.files.internal("bulbasaur.atlas"))));
        animations.put(Type.SQUIRTLE, generate(new TextureAtlas(Gdx.files.internal("squirtle.atlas"))));
    }

    private Map<Direction, Animation> generate(TextureAtlas atlas){
        Map<Direction, Animation> map = new HashMap<Direction, Animation>();

        // Generate animations types (down, left, up, right)
        map.put(Direction.DOWN, new Animation(ANIM_RATE, atlas.findRegion("down1"), atlas.findRegion("down2")));
        map.put(Direction.LEFT, new Animation(ANIM_RATE, atlas.findRegion("left1"), atlas.findRegion("left2")));
        map.put(Direction.UP, new Animation(ANIM_RATE, atlas.findRegion("up1"), atlas.findRegion("up2")));
        map.put(Direction.RIGHT, new Animation(ANIM_RATE, atlas.findRegion("right1"), atlas.findRegion("right2")));

        return map;
    }

    public Animation getAnimation(Type type, Direction direction) {
        return animations.get(type).get(direction);
    }
}
