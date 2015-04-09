package engine;

import com.badlogic.gdx.Gdx;
import objects.PlayerType;

public class Config {
    /* ====== General ====== */
    // Game
    public static final boolean DEBUG = false;
    public static final boolean USE_EXTERNAL_ANIMS = false;

    //    public static final String VERSION = "Pre-alpha v0.4: Collision DLC";
    public static final String VERSION = "Clarisse Mode";
    public static final PlayerType DEFAULT_TYPE = PlayerType.CHARMANDER;
    public static final String MAP = "overworld.png";
    public static final float SPAWN_X = 865;
    public static final float SPAWN_Y = 550;

    // Networking
    public static final int GAME_PORT = 5284;
    public static final int PACKET_SIZE = 512;
    public static final String SERVER_IP = "localhost";

    /* ====== Sensitive ====== */
    // Engine
    public static final float WALK_DIST = 5f;
    public static final int TILE_SIZE = 16;
    public static final float VIEWPORT_WIDTH = Gdx.graphics.getWidth() / 2f;
    public static final float VIEWPORT_HEIGHT = Gdx.graphics.getHeight() / 2f;
    public static final float UPDATE_RATE = 1 / 15f;
    public static final float ANIM_RATE = 1 / 2f; // 2.5f is
    public static final float CHAR_WIDTH = 11f;
    public static final float CHAR_HEIGHT = 14f;
    public static final float CHAR_COLL_WIDTH = 11f;
    public static final float CHAR_COLL_HEIGHT = 12f;

    // Rendering
    public static final float ANIM_DURATION = 1 / 5f; // 6f is decent
    public static final float RENDER_OFFSET_X = 4f;
    public static final float USERNAME_PADDING_Y = 10f;
    public static final float FONT_HEIGHT = 8f;

    // Camera
    public static final float CAM_MIN_X = (VIEWPORT_WIDTH / 2f);
    public static final float CAM_MIN_Y = (VIEWPORT_HEIGHT / 2f);
    public static final float CAM_MAX_X = AssetManager.level.getWidth() - (VIEWPORT_WIDTH / 2f);
    public static final float CAM_MAX_Y = AssetManager.level.getHeight() - (VIEWPORT_HEIGHT / 2f);

    // Chat System
    public static final int MAX_CHAT_ROWS = 6;
    public static final int MESSAGES_INIT = 200;
    public static final int MAX_MSG_LENGTH = 100;
    public static final String DATE_FORMAT = "h:mm:ss a";
    public static final String DATE_FORMAT_CHAT = "h:mm a";
}
