package engine;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import objects.PlayerType;

public class UserInputProcessor implements InputProcessor {

    /* Note: These values must be read another class
     * to update the values for the main player. */
    public static boolean[] directionKeys; // Down-Left-Up-Right
    public static boolean[] attackKeys; // Melee-Ranged
    public static PlayerType selectedType;
    private Core core;

    public UserInputProcessor(Core core) {
        this.core = core;
        selectedType = Config.DEFAULT_TYPE;
        directionKeys = new boolean[4];
        attackKeys = new boolean[2];
    }

    @Override
    public boolean keyDown(int keycode) {
        if (core.getUI().hasFocus()) {
            return false;
        }
        switch (keycode) {
            case Input.Keys.DOWN:
                directionKeys[0] = true;
                break;
            case Input.Keys.LEFT:
                directionKeys[1] = true;
                break;
            case Input.Keys.UP:
                directionKeys[2] = true;
                break;
            case Input.Keys.RIGHT:
                directionKeys[3] = true;
                break;
            // Disabling melee
//            case Input.Keys.Z:
//                attackKeys[0] = true;
//                break;
            case Input.Keys.X:
                attackKeys[1] = true;
                break;
            case Input.Keys.NUM_1:
                selectedType = PlayerType.CHARMANDER;
                core.getPlayers().getMainPlayer().getAnim().updateAnim();
                break;
            case Input.Keys.NUM_2:
                selectedType = PlayerType.BULBASAUR;
                core.getPlayers().getMainPlayer().getAnim().updateAnim();
                break;
            case Input.Keys.NUM_3:
                selectedType = PlayerType.SQUIRTLE;
                core.getPlayers().getMainPlayer().getAnim().updateAnim();
                break;
            case Input.Keys.ENTER:
                core.getUI().showChat(true);
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (core.getUI().hasFocus()) {
            return false;
        }
        switch (keycode) {
            case Input.Keys.DOWN:
                directionKeys[0] = false;
                break;
            case Input.Keys.LEFT:
                directionKeys[1] = false;
                break;
            case Input.Keys.UP:
                directionKeys[2] = false;
                break;
            case Input.Keys.RIGHT:
                directionKeys[3] = false;
                break;
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
