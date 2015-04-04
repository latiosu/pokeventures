package editable;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import engine.*;
import objects.Direction;
import objects.Player;
import objects.Type;

public class Logic {

    Player player;
    OrthographicCamera cam;

    float mapWidth, mapHeight, maxCamX, maxCamY;

    public Logic(Player p, OrthographicCamera cam){
        player = p;
        this.cam = cam;

        mapWidth = 1280f;
        mapHeight = 1310f;
        maxCamX = (mapWidth/2f - cam.viewportWidth);
        maxCamY = (mapHeight/2f - cam.viewportHeight);
    }

    // Add any game logic you'd like here
    public void update(){
        updatePlayer();
        updateCamera();
    }

    public void changeType(int keycode) {
        switch (keycode) {
            case Input.Keys.NUM_1:
                player.setType(Type.CHARMANDER);
                break;
            case Input.Keys.NUM_2:
                player.setType(Type.BULBASAUR);
                break;
            case Input.Keys.NUM_3:
                player.setType(Type.SQUIRTLE);
                break;
        }
    }

    private void updatePlayer() {
        // Input logic
        boolean[] keys = KeyboardProcessor.directionKeys;
        if(!keys[0] && !keys[1] && !keys[2] && !keys[3]) {
            player.setMoving(false);
        } else if (keys[0]) {
            player.move(Direction.DOWN);
        } else if (keys[1]) {
            player.move(Direction.LEFT);
        } else if (keys[2]) {
            player.move(Direction.UP);
        } else if (keys[3]) {
            player.move(Direction.RIGHT);
        }

        // Position logic
        if(player.isMoving()) {
            player.getAnim().play();
            switch (player.getDirection()) {
                case DOWN:
                    player.setY(player.getY() - Config.WALK_DIST);
                    break;
                case LEFT:
                    player.setX(player.getX() - Config.WALK_DIST);
                    break;
                case UP:
                    player.setY(player.getY() + Config.WALK_DIST);
                    break;
                case RIGHT:
                    player.setX(player.getX() + Config.WALK_DIST);
                    break;
            }
        }
    }

    private void updateCamera() {

        /*
         * Add code which updates camera position based
         * on half-screen threshold.
         */

        if(player.isMoving()) {
            switch (player.getDirection()) {
                case DOWN:
                    cam.translate(0, -Config.WALK_DIST);
                    break;
                case LEFT:
                    cam.translate(-Config.WALK_DIST, 0);
                    break;
                case UP:
                    cam.translate(0, Config.WALK_DIST);
                    break;
                case RIGHT:
                    cam.translate(Config.WALK_DIST, 0);
                    break;
            }
            cam.update();
        }
    }

}
