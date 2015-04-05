package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import objects.Direction;
import objects.Type;

import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    public static Map<Type, Map<Direction, Animation>> animations;
    public static Skin skin;
    public static Texture level;
    public static BitmapFont font;


    public AssetManager() {
        animations = new HashMap<Type, Map<Direction, Animation>>();
        loadAssets();
    }

    private void loadAssets() {
        // Load UI components
        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
        // Load Fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/pokemon12.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        parameter.characters = "abcdefghijklmnopqrstuvwxyz";
        font = generator.generateFont(parameter);
        generator.dispose();
        // Load overworld
        level = new Texture(Gdx.files.internal("assets/overworld.png"));
        // Load character animations
        animations.put(Type.CHARMANDER, generate(new TextureAtlas(Gdx.files.internal("assets/charmander.atlas"))));
        animations.put(Type.BULBASAUR, generate(new TextureAtlas(Gdx.files.internal("assets/bulbasaur.atlas"))));
        animations.put(Type.SQUIRTLE, generate(new TextureAtlas(Gdx.files.internal("assets/squirtle.atlas"))));
    }

    private Map<Direction, Animation> generate(TextureAtlas atlas){
        Map<Direction, Animation> map = new HashMap<Direction, Animation>();

        // Generate animations types (down, left, up, right)
        map.put(Direction.DOWN, new Animation(Config.ANIM_RATE, atlas.findRegion("down1"), atlas.findRegion("down2")));
        map.put(Direction.LEFT, new Animation(Config.ANIM_RATE, atlas.findRegion("left1"), atlas.findRegion("left2")));
        map.put(Direction.UP, new Animation(Config.ANIM_RATE, atlas.findRegion("up1"), atlas.findRegion("up2")));
        map.put(Direction.RIGHT, new Animation(Config.ANIM_RATE, atlas.findRegion("right1"), atlas.findRegion("right2")));

        return map;
    }

    public static Animation getAnimation(Type type, Direction direction) {
        return animations.get(type).get(direction);
    }
}
