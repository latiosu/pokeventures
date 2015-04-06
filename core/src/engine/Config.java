package engine;

import objects.Entity;

public class Config {
    /* ====== General ====== */
    // Game
    public static final Entity.Type DEFAULT_TYPE = Entity.Type.CHARMANDER;
    public static final float SPAWN_X = -864;
    public static final float SPAWN_Y = -550;

    // Networking
    public static final int GAME_PORT = 5284;
    public static final int PACKET_SIZE = 512;
    public static final String SERVER_IP = "localhost";

    /* ====== Sensitive ====== */
    // Engine
    public static final float GAME_RES_WIDTH = 480;
    public static final float GAME_RES_HEIGHT = 320;
    public static final float GAME_WIDTH = 480*2f;
    public static final float GAME_HEIGHT = 320*2f;
    public static final float UPDATE_RATE = 1/15f;
    public static final float ANIM_RATE = 1/2f;

    // Rendering
    public static final float WALK_DIST = 5f;
    public static final float ANIM_DURATION = 1/5f;
    public static final float CHAR_WIDTH = 11f;
    public static final float CHAR_HEIGHT = 16f;
    public static final float RENDER_OFFSET_X = 4f;
    public static final float USERNAME_PADDING_Y = 10f;
    public static final float FONT_HEIGHT = 8f;

    // Chat System
    public static final int MAX_CHAT_ROWS = 6;
    public static final int MESSAGES_INIT = 200;
    public static final int MAX_MSG_LENGTH = 100;
    public static final String DATE_FORMAT = "h:mm:ss a";
    public static final String DATE_FORMATE_CHAT = "h:mm a";
}
