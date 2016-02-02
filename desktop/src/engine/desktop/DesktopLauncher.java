package engine.desktop;

import com.badlogic.gdx.Files;
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
            Config.ASSETS_PATH = "core/assets/";
        }
        if (list.contains("pack-textures")) {
            Config.PACK_TEXTURES = true;
        }
        if (list.contains("debug")) {
            Config.DEBUG = true;
        }
        if (list.contains("offline")) {
            Config.Networking.SERVER_IP = "localhost";
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
            config.addIcon(Config.ASSETS_PATH + "pokeventures_128x128.png", Files.FileType.Internal);
            config.addIcon(Config.ASSETS_PATH + "pokeventures_32x32.png", Files.FileType.Internal);
            config.addIcon(Config.ASSETS_PATH + "pokeventures_16x16.png", Files.FileType.Internal);

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
