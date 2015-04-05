package engine;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import objects.Entity;
import objects.Player;

public class KeyboardProcessor implements InputProcessor {

    /* Note: These values must be read another class
     * to update the values for the main player. */
    public static boolean[] directionKeys; // Down Left Up Right
    public static Player.Type selectedType;
    private Core core;

    public KeyboardProcessor(Core core) {
        this.core = core;
        this.selectedType = Entity.Type.CHARMANDER; // <-------- HARDCODED
        directionKeys = new boolean[4];
    }

    @Override
    public boolean keyDown(int keycode) {
        if(core.ui.hasFocus()) {
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
            case Input.Keys.NUM_1:
                selectedType = Entity.Type.CHARMANDER;
                break;
            case Input.Keys.NUM_2:
                selectedType = Entity.Type.BULBASAUR;
                break;
            case Input.Keys.NUM_3:
                selectedType = Entity.Type.SQUIRTLE;
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(core.ui.hasFocus()) {
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
