package engine;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import objects.Entity;
import objects.Player;

public class UserInputProcessor implements InputProcessor {

    /* Note: These values must be read another class
     * to update the values for the main player. */
    public static boolean[] directionKeys; // Down Left Up Right
    public static Player.Type selectedType;
    private Core core;

    public UserInputProcessor(Core core) {
        this.core = core;
        selectedType = Config.DEFAULT_TYPE;
        directionKeys = new boolean[4];
    }

    @Override
    public boolean keyDown(int keycode) {
        if(core.getUI().hasFocus()) {
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
                core.getPlayers().getMainPlayer().getAnim().changeAnim();
                break;
            case Input.Keys.NUM_2:
                selectedType = Entity.Type.BULBASAUR;
                core.getPlayers().getMainPlayer().getAnim().changeAnim();
                break;
            case Input.Keys.NUM_3:
                selectedType = Entity.Type.SQUIRTLE;
                core.getPlayers().getMainPlayer().getAnim().changeAnim();
                break;
            case Input.Keys.ENTER:
                core.getUI().showChat(true);
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(core.getUI().hasFocus()) {
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
