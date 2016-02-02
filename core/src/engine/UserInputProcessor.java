package engine;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import objects.structs.PlayerType;

public class UserInputProcessor implements InputProcessor {

    /* Note: These values must be read another class
     * to update the values for the main player. */
    public static boolean[] directionKeys; // Down-Left-Up-Right
    public static boolean[] attackKeys; // None-Ranged
    public static PlayerType selectedType;

    private ClientCore clientCore;

    public UserInputProcessor(ClientCore clientCore) {
        this.clientCore = clientCore;
        selectedType = Config.Character.DEFAULT_TYPE;
        directionKeys = new boolean[4];
        attackKeys = new boolean[2];
    }

    @Override
    public boolean keyDown(int keycode) {
        // Capture keystrokes in chat
        if (clientCore.getUI().hasFocus()) {
            return false;
        }

        // Movement
        if (keycode == Input.Keys.DOWN) {
            directionKeys[0] = true;
        }
        if (keycode == Input.Keys.LEFT) {
            directionKeys[1] = true;
        }
        if (keycode == Input.Keys.UP) {
            directionKeys[2] = true;
        }
        if (keycode == Input.Keys.RIGHT) {
            directionKeys[3] = true;
        }

        // Attack
        if (keycode == Input.Keys.SPACE) {
            attackKeys[1] = true;
        }

        // Disabled until implemented
        // Change characters
//        if (keycode == Input.Keys.NUM_1) {
//            selectedType = PlayerType.CHARMANDER;
//            clientCore.getPlayers().getMainPlayer().getAnim().updateAnim();
//        }
//        if (keycode == Input.Keys.NUM_2) {
//            selectedType = PlayerType.BULBASAUR;
//            clientCore.getPlayers().getMainPlayer().getAnim().updateAnim();
//        }
//        if (keycode == Input.Keys.NUM_3) {
//            selectedType = PlayerType.SQUIRTLE;
//            clientCore.getPlayers().getMainPlayer().getAnim().updateAnim();
//        }

        // Chat
        if (keycode == Input.Keys.ENTER) {
            clientCore.getUI().clientCore.showChat(true);
        }

        // Debug commands
        if (Config.DEBUG) {
            if (keycode == Input.Keys.P) {
                clientCore.getPlayers().getMainPlayer().setAlive(false);
            }
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (clientCore.getUI().hasFocus()) {
            return false;
        }

        // Movement
        if (keycode == Input.Keys.DOWN) {
            directionKeys[0] = false;
        }
        if (keycode == Input.Keys.LEFT) {
            directionKeys[1] = false;
        }
        if (keycode == Input.Keys.UP) {
            directionKeys[2] = false;
        }
        if (keycode == Input.Keys.RIGHT) {
            directionKeys[3] = false;
        }

        // Attack
        if (keycode == Input.Keys.SPACE) {
            attackKeys[1] = false;
        }

        // Chat
        if (keycode == Input.Keys.ENTER) {
            clientCore.getUI().clientCore.showChat(false);
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    // Not used
    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
