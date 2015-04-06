package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import objects.Entity;
import objects.Player;

import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    public static Map<Player.Type, Map<Entity.Direction, Animation>> animations;
    public static Skin skin;
    public static Texture level;
    public static BitmapFont font;

    public AssetManager() {
        animations = new HashMap<Player.Type, Map<Entity.Direction, Animation>>();
        loadAssets();
    }

    private void loadAssets() {
        // Load UI components
        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
        // Load Fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/text.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        font = generator.generateFont(parameter);
        generator.dispose();
        // Load overworld
        level = new Texture(Gdx.files.internal("assets/overworld.png"));
        // Load character animations
        animations.put(Entity.Type.CHARMANDER, generate(new TextureAtlas(Gdx.files.internal("assets/charmander.atlas"))));
        animations.put(Entity.Type.BULBASAUR, generate(new TextureAtlas(Gdx.files.internal("assets/bulbasaur.atlas"))));
        animations.put(Entity.Type.SQUIRTLE, generate(new TextureAtlas(Gdx.files.internal("assets/squirtle.atlas"))));
    }

    private Map<Entity.Direction, Animation> generate(TextureAtlas atlas){
        Map<Entity.Direction, Animation> map = new HashMap<Entity.Direction, Animation>();

        // Generate animations types (down, left, up, right)
        map.put(Entity.Direction.DOWN, new Animation(Config.ANIM_DURATION, atlas.findRegion("down1"), atlas.findRegion("down2")));
        map.put(Entity.Direction.LEFT, new Animation(Config.ANIM_DURATION, atlas.findRegion("left1"), atlas.findRegion("left2")));
        map.put(Entity.Direction.UP, new Animation(Config.ANIM_DURATION, atlas.findRegion("up1"), atlas.findRegion("up2")));
        map.put(Entity.Direction.RIGHT, new Animation(Config.ANIM_DURATION, atlas.findRegion("right1"), atlas.findRegion("right2")));

        return map;
    }

    public static Animation getAnimation(Player.Type type, Entity.Direction direction) {
        return animations.get(type).get(direction);
    }
}
