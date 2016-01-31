package engine.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import engine.Config;
import engine.ClientCore;
import server.ServerCore;
import objects.structs.PlayerType;

public class DesktopLauncher {
	public static void main (String[] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
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
                        if (Config.USE_EXTERNAL_ANIMS) {
                            TexturePacker.process("to-be-packed/" + pt.getName(), "packed", pt.getName());
                        } else {
                            TexturePacker.process("assets/to-be-packed/" + pt.getName(), "assets/packed", pt.getName());
                        }
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
