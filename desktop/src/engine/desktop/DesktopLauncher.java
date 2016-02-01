package engine.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import engine.ClientCore;
import engine.Config;
import objects.structs.PlayerType;
import server.ServerCore;

import java.util.Arrays;
import java.util.List;

public class DesktopLauncher {
	public static void main (String[] args) {
        List<String> list = Arrays.asList(args);

        // Command-line args processing
        if (list.contains("wip")) {
            Config.ASSETS_PATH = "assets/";
        }
        if (list.contains("pack-textures")) {
            Config.PACK_TEXTURES = true;
        }
        if (list.contains("debug")) {
            Config.DEBUG = true;
        }

        // Start either client or server
        if (list.contains("server")) {
            new ServerCore().start();
        } else {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.title = "PokeVentures";
            config.width = 480 * 2;
            config.height = 320 * 2;
            config.resizable = false;

            try {
                // Pack textures
                if (Config.PACK_TEXTURES) {
                    for (PlayerType pt : PlayerType.values()) {
                        TexturePacker.process(Config.ASSETS_PATH + "to-be-packed/" + pt.getName(),
                                Config.ASSETS_PATH + "packed", pt.getName()
                        );
                    }
                }

                // Start game
                new LwjglApplication(new ClientCore(), config);

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
	}
}
