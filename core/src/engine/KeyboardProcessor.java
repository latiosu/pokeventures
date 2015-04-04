package engine;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class KeyboardProcessor implements InputProcessor {

    public static boolean[] directionKeys; // Down Left Up Right
    private Logic logic;

    public KeyboardProcessor(Logic logic) {
        this.logic = logic;
        directionKeys = new boolean[4];
    }

    @Override
    public boolean keyDown(int keycode) {
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
            case Input.Keys.NUM_1:
            case Input.Keys.NUM_2:
            case Input.Keys.NUM_3:
                logic.changeType(keycode);
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
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
    public boolean keyTyped(char character) {
        return false;
    }

    // Not used
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
