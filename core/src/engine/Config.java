package engine;

import com.badlogic.gdx.Gdx;
import objects.structs.PlayerType;

public class Config {
    /* ====== General ====== */
    // Game
    public static boolean DEBUG = false;
    public static boolean PACK_TEXTURES = false;
    public static String ASSETS_PATH = "";

    public static final String VERSION = "Pre-alpha v0.70: Cool battle stuff!";
    public static final PlayerType DEFAULT_TYPE = PlayerType.CHARMANDER;
    public static final String MAP = "overworld.png";
    public static final float SPAWN_X = 865;
    public static final float SPAWN_Y = 550;
    public static final float PLAYER_MAX_HP = 100;

    // Networking
    public static final int GAME_PORT = 5284; // Default: 5284, Skype: 37425
    public static final int PACKET_SIZE = 512;
    public static final String SERVER_IP = "localhost";

    /* ====== Sensitive ====== */
    // Engine
    public static final float WALK_DIST = 5f;
    public static final int TILE_SIZE = 16;

    public static final float UPDATE_RATE = 1 / 15f;
    public static final float CHAR_WIDTH = 11f;
    public static final float CHAR_HEIGHT = 14f;
    public static final float CHAR_COLL_WIDTH = 11f;
    public static final float CHAR_COLL_HEIGHT = 12f;

    // Animations
    public static final float ANIM_DURATION = 1 / 5f; // 200ms

    // Rendering
    public static final float RENDER_OFFSET_X = 4f;
    public static final float USERNAME_PADDING_Y = 10f;
    public static final float FONT_HEIGHT = 8f;

    // Camera
    public static class Camera {
        public static final float VIEWPORT_WIDTH = Gdx.graphics.getWidth() / 2f;
        public static final float VIEWPORT_HEIGHT = Gdx.graphics.getHeight() / 2f;
        public static final float CAM_MIN_X = (VIEWPORT_WIDTH / 2f);
        public static final float CAM_MIN_Y = (VIEWPORT_HEIGHT / 2f);
        public static final float CAM_MAX_X = AssetManager.level.getWidth() - (VIEWPORT_WIDTH / 2f);
        public static final float CAM_MAX_Y = AssetManager.level.getHeight() - (VIEWPORT_HEIGHT / 2f);
    }

    // Chat System
    public static final int MAX_CHAT_ROWS = 6;
    public static final int MESSAGES_INIT = 200;
    public static final int MAX_MSG_LENGTH = 100;
    public static final String DATE_FORMAT = "h:mm:ss a";
    public static final String DATE_FORMAT_CHAT = "h:mm a";

    // Character Stats
    public static final double CHARMANDER_MAXASPD = 3; // Attacks per second
    public static final double BULBASAUR_MAXASPD = 3;
    public static final double SQUIRTLE_MAXASPD = 3;

    public static final float CHARMANDER_ATKRANGE = 85;
    public static final float BULBASAUR_ATKRANGE = 85;
    public static final float SQUIRTLE_ATKRANGE = 85;

    public static final float CHARMANDER_ATKMSPD = 10;
    public static final float BULBASAUR_ATKMSPD = 10;
    public static final float SQUIRTLE_ATKMSPD = 10;

    public static final float CHARMANDER_ATKOFFSET = 4;
    public static final float BULBASAUR_ATKOFFSET = 4;
    public static final float SQUIRTLE_ATKOFFSET = 4;

    public static final float CHARMANDER_ATKDMG = 12.5f;
    public static final float BULBASAUR_ATKDMG = 12.5f;
    public static final float SQUIRTLE_ATKDMG = 12.5f;


    // Debug Stuff
    public static final int DEBUG_LOG_RATE = 3; // Rate of which stats are logged to console (seconds)

}
