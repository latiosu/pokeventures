package engine.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import engine.Config;
import engine.Core;
import objects.PlayerType;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "PokeVentures";
        config.width = 480*2;
        config.height = 320*2;
        config.resizable = false;

        PlayerType[] ptypes = { PlayerType.CHARMANDER, PlayerType.BULBASAUR, PlayerType.SQUIRTLE };
        for(int i=0; i< ptypes.length; i++) {
            if(Config.USE_EXTERNAL_ANIMS){
                TexturePacker.process("to-be-packed/" + ptypes[i].getName(), "packed", ptypes[i].getName());
            } else {
                TexturePacker.process("assets/to-be-packed/" + ptypes[i].getName(), "assets/packed", ptypes[i].getName());
            }
        }

        new LwjglApplication(new Core(), config);
	}
}
