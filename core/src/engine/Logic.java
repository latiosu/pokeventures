package engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import networking.packets.Packet02Move;
import objects.Direction;
import objects.Player;

public class Logic {

    Core core;
    OrthographicCamera cam;

    float mapWidth, mapHeight, maxCamX, maxCamY;

    public Logic(Core core, OrthographicCamera cam){
        this.core = core;
        this.cam = cam;

        mapWidth = 1280f;
        mapHeight = 1310f;
        maxCamX = (mapWidth/2f - cam.viewportWidth);
        maxCamY = (mapHeight/2f - cam.viewportHeight);
    }

    public void update(){
        updateMainPlayer(); // Client-side rendering update
        updateCamera();     // Same as above
    }

    private void updateMainPlayer() {
        // Input logic
        boolean[] keys = KeyboardProcessor.directionKeys;
        if(!keys[0] && !keys[1] && !keys[2] && !keys[3]) {
            core.getMainPlayer().setMoving(false);
        } else if (keys[0]) {
            core.getMainPlayer().move(Direction.DOWN);
        } else if (keys[1]) {
            core.getMainPlayer().move(Direction.LEFT);
        } else if (keys[2]) {
            core.getMainPlayer().move(Direction.UP);
        } else if (keys[3]) {
            core.getMainPlayer().move(Direction.RIGHT);
        }

        // Position logic
        if(core.getMainPlayer().isMoving()) {
            switch (core.getMainPlayer().getDirection()) {
                case DOWN:
                    core.getMainPlayer().setY(core.getMainPlayer().getY() - Config.WALK_DIST);
                    break;
                case LEFT:
                    core.getMainPlayer().setX(core.getMainPlayer().getX() - Config.WALK_DIST);
                    break;
                case UP:
                    core.getMainPlayer().setY(core.getMainPlayer().getY() + Config.WALK_DIST);
                    break;
                case RIGHT:
                    core.getMainPlayer().setX(core.getMainPlayer().getX() + Config.WALK_DIST);
                    break;
            }
        }

        // Send movement packet to server with integer substitutions
        Packet02Move packet = new Packet02Move(core.getMainPlayer().getUsername(), core.getMainPlayer().getX(),
                core.getMainPlayer().getY(), core.getMainPlayer().isMovingInt(),
                core.getMainPlayer().getDirection().getNum(), core.getMainPlayer().getType().getNum());
        packet.writeData(core.client);
    }

    private void updateCamera() {

        /*
         * Add code which updates camera position based
         * on half-screen threshold.
         */

        if(core.getMainPlayer().isMoving()) {
            switch (core.getMainPlayer().getDirection()) {
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
