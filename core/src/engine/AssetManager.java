package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import objects.Direction;
import objects.PlayerType;
import objects.State;

import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    public static Map<PlayerType, Map<String, Animation>> animations;
    public static Skin skin;
    public static Texture level;
    public static BitmapFont font;
    public static Texture setupBG;

    public AssetManager() {
        animations = new HashMap<PlayerType, Map<String, Animation>>();
        loadAssets();
    }

    private void loadAssets() {
        // Load UI components
        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
        setupBG = new Texture(Gdx.files.internal("assets/setup-bg.png"));
        // Load Fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/text.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        font = generator.generateFont(parameter);
        generator.dispose();
        // Load overworld
        level = new Texture(Gdx.files.internal("assets/" + Config.MAP));
        // Load character animations
        PlayerType[] ptypes = { PlayerType.CHARMANDER, PlayerType.BULBASAUR, PlayerType.SQUIRTLE }; // Could change PType to an EnumMap
        for(int i=0; i<ptypes.length; i++) {
            if(Config.USE_EXTERNAL_ANIMS) {
                animations.put(ptypes[i], generate(new TextureAtlas(Gdx.files.internal("packed/" + ptypes[i].getName() + ".atlas"))));
            } else {
                animations.put(ptypes[i], generate(new TextureAtlas(Gdx.files.internal("assets/packed/" + ptypes[i].getName() + ".atlas"))));
            }
        }
    }

    // PlayerType > State > Direction
    private Map<String, Animation> generate(TextureAtlas atlas){
        Map<String, Animation> map = new HashMap<String, Animation>();

        String[] states = {"idle-", "walk-", "melee-", "ranged-"}; // ORDER MATTERS (used for if-statement)
        String[] dirs = {"down", "left", "up", "right"};
        for(int i=0; i<states.length; i++) {
            for(int j=0; j<dirs.length; j++) {
                map.put(states[i] + dirs[j], new Animation(Config.ANIM_DURATION, atlas.findRegions(states[i] + dirs[j])));
            }
        }
        return map;
    }

    public static Animation getAnimation(PlayerType type, State state, Direction direction) {
        String animName = "";
        switch (state) {
            case IDLE:
                animName += "idle-";
                break;
            case WALK:
                animName += "walk-";
                break;
            case ATK_MELEE:
                animName += "melee-";
                break;
            case ATK_RANGED:
                animName += "ranged-";
                break;
        }
        switch (direction) {
            case DOWN:
                animName += "down";
                break;
            case LEFT:
                animName += "left";
                break;
            case UP:
                animName += "up";
                break;
            case RIGHT:
                animName += "right";
                break;
        }
        return animations.get(type).get(animName);
    }
}
