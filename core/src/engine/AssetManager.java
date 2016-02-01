package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import objects.structs.Direction;
import objects.structs.PlayerType;
import objects.structs.State;

import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    public static Map<PlayerType, Map<String, Animation>> animations;
    public static Skin skin;
    public static Texture level;
    public static BitmapFont font;
    public static Texture setupBG;

    public AssetManager() {
        animations = new HashMap<>();
        loadAssets();
    }

    public static Animation getAnimation(PlayerType type, State state, Direction direction, boolean isAttack) {
        String animName = "";
        switch (state) {
            case IDLE:
                animName += "idle";
                break;
            case WALK:
                animName += "walk";
                break;
            case JUMP:
                animName += "jump";
                break;
            case ATK_RANGED:
                animName += "ranged";
                break;
            case FAINTED:
                animName += "fainted";
                break;
        }
        switch (direction) {
            case DOWN:
                animName += "-down";
                break;
            case LEFT:
                animName += "-left";
                break;
            case UP:
                animName += "-up";
                break;
            case RIGHT:
                animName += "-right";
                break;
        }
        if (isAttack) {
            animName += "-attack";
        }

        // Log error if null
        Animation result = animations.get(type).get(animName);
        if (result == null) {
            Logger.log(Logger.Level.ERROR,
                    "Animation could not be found (%s)\n",
                    animName);
        }
        return result;
    }

    private void loadAssets() {
        // Load UI components
        skin = new Skin(Gdx.files.internal(Config.ASSETS_PATH + "uiskin.json"));
        setupBG = new Texture(Gdx.files.internal(Config.ASSETS_PATH + "setup-bg.png"));
        // Load Fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Config.ASSETS_PATH + "text.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        font = generator.generateFont(parameter);
        generator.dispose();
        // Load overworld
        level = new Texture(Gdx.files.internal(Config.ASSETS_PATH + Config.World.MAP));
        // Load character animations
        for (PlayerType pt : PlayerType.values()) {
            animations.put(pt, generate(new TextureAtlas(Gdx.files.internal(
                    Config.ASSETS_PATH + "packed/" + pt.getName() + ".atlas"
            ))));
        }
    }

    // PlayerType > State > Direction > Null/Attack
    private Map<String, Animation> generate(TextureAtlas atlas) {
        Map<String, Animation> map = new HashMap<>();

        String[] states = {"idle", "walk", "jump", "ranged", "fainted"};
        String[] dirs = {"-down", "-left", "-up", "-right"};
        String[] attacks = {"", "-attack"};
        for (String s : states) {
            for (String d : dirs) {
                for (String a : attacks) {
                    map.put(s + d + a, new Animation(Config.Rendering.ANIM_DURATION, atlas.findRegions(s + d + a)));
                }
            }
        }
        return map;
    }
}
