package engine;

import com.badlogic.gdx.Gdx;
import objects.structs.PlayerType;

import java.util.concurrent.TimeUnit;

public class Config {

    public static boolean DEBUG = false;
    public static boolean PACK_TEXTURES = false;
    public static String ASSETS_PATH = "";

    public static class Engine {
        public static final String VERSION = "Pre-alpha v0.80: OMG BATTLES!";
        public static final float CLIENT_UPDATE_RATE = 1 / 15f;
        public static final float SERVER_UPDATE_RATE = 1 / 40f;
        public static final int DEBUG_LOG_RATE = 3; // Rate of which stats are logged to console (seconds)
        public static final float SCORE_UPDATE_RATE = 1f; // Seconds scores every second
        public static final int MAX_USERNAME_LENGTH = 11;

        // Chat
        public static final int MAX_CHAT_ROWS = 6;
        public static final int MESSAGES_INIT = 200;
        public static final int MAX_MSG_LENGTH = 100;
        public static final String DATE_FORMAT = "h:mm:ss a";
        public static final String DATE_FORMAT_CHAT = "h:mm a";

        // Scoreboard
        public static final int MAX_SCORE_ROWS = 8;
    }

    public static class World {
        public static final String MAP = "overworld.png";
        public static final float SPAWN_X = 299;
        public static final float SPAWN_Y = 370;
        public static final int TILE_SIZE = 16;
    }

    public static class Networking {
        public static final int GAME_PORT = 5284; // Default: 5284
        public static final int PACKET_SIZE = 512;
        public static final String SERVER_IP = "localhost";
        public static final long HEARTBEAT_WAIT_TIME = TimeUnit.SECONDS.toNanos(5);
        public static final float HEARTBEAT_RATE = 1; // Send heartbeat each second
    }

    public static class Rendering {
        public static final float ANIM_DURATION = 1 / 5f; // 200ms
        public static final float RENDER_OFFSET_X = 4f;
        public static final float USERNAME_PADDING_Y = 10f;
        public static final float FONT_HEIGHT = 8f;
    }

    public static class Camera {
        public static final float VIEWPORT_WIDTH = Gdx.graphics.getWidth() / 2f;
        public static final float VIEWPORT_HEIGHT = Gdx.graphics.getHeight() / 2f;
        public static final float CAM_MIN_X = (VIEWPORT_WIDTH / 2f);
        public static final float CAM_MIN_Y = (VIEWPORT_HEIGHT / 2f);
        public static final float CAM_MAX_X = AssetManager.level.getWidth() - (VIEWPORT_WIDTH / 2f);
        public static final float CAM_MAX_Y = AssetManager.level.getHeight() - (VIEWPORT_HEIGHT / 2f);
    }

    public static class Character {
        public static final PlayerType DEFAULT_TYPE = PlayerType.CHARMANDER;

        public static final float PLAYER_MAX_HP = 100;

        public static final float WALK_DIST = 5f;

        public static final float CHAR_HEIGHT = 14f;
        public static final float CHAR_WIDTH = 11f;
        public static final float CHAR_COLL_WIDTH = 11f;
        public static final float CHAR_COLL_HEIGHT = 12f;

        public static final double CHARMANDER_MAXASPD = 1.5; // Attacks per second
        public static final double BULBASAUR_MAXASPD = 1.5;
        public static final double SQUIRTLE_MAXASPD = 1.5;

        public static final float CHARMANDER_ATKRANGE = 85;
        public static final float BULBASAUR_ATKRANGE = 85;
        public static final float SQUIRTLE_ATKRANGE = 85;

        public static final float CHARMANDER_ATKMSPD = 8;
        public static final float BULBASAUR_ATKMSPD = 8;
        public static final float SQUIRTLE_ATKMSPD = 8;

        public static final float CHARMANDER_ATKOFFSET = 12;
        public static final float BULBASAUR_ATKOFFSET = 12;
        public static final float SQUIRTLE_ATKOFFSET = 12;

        public static final float CHARMANDER_ATKDMG = 12.5f;
        public static final float BULBASAUR_ATKDMG = 12.5f;
        public static final float SQUIRTLE_ATKDMG = 12.5f;
    }
}
