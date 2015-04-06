package engine.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import engine.Core;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "PokeVentures";
        config.width = 480*2;
        config.height = 320*2;
        config.resizable = false;

        new LwjglApplication(new Core(), config);
	}
}
