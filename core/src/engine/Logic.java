package engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import networking.packets.Packet02Move;
import objects.Entity;
import objects.Player;

public class Logic {

    Core core;
    OrthographicCamera cam;

    float mapWidth, mapHeight, maxCamX, maxCamY;

    public Logic(Core core, OrthographicCamera cam){
        this.core = core;
        this.cam = cam;

//        mapWidth = 1280f;
//        mapHeight = 1310f;
//        maxCamX = (mapWidth/2f - cam.viewportWidth);
//        maxCamY = (mapHeight/2f - cam.viewportHeight);
    }

    /**
     * Note: These are client-sided rendering updates
     */
    public void update(Player mp){
        updateMainPlayer(mp);
        updateCamera(mp);
    }

    private void updateMainPlayer(Player mp) {
        // Input logic
        boolean[] keys = UserInputProcessor.directionKeys;
        mp.setType(UserInputProcessor.selectedType);
        if(!keys[0] && !keys[1] && !keys[2] && !keys[3]) {
            mp.setMoving(false);
        } else if (keys[0]) {
            mp.move(Entity.Direction.DOWN);
        } else if (keys[1]) {
            mp.move(Entity.Direction.LEFT);
        } else if (keys[2]) {
            mp.move(Entity.Direction.UP);
        } else if (keys[3]) {
            mp.move(Entity.Direction.RIGHT);
        }

        // Position logic
        if(mp.isMoving()) {
            switch (mp.getDirection()) {
                case DOWN:
                    mp.setY(mp.getY() - Config.WALK_DIST);
                    break;
                case LEFT:
                    mp.setX(mp.getX() - Config.WALK_DIST);
                    break;
                case UP:
                    mp.setY(mp.getY() + Config.WALK_DIST);
                    break;
                case RIGHT:
                    mp.setX(mp.getX() + Config.WALK_DIST);
                    break;
            }
        }

        // Send movement packet to server with integer substitutions
        sendUpdatePacket(mp);
    }

    private void updateCamera(Player mp) {

        /*
         * Add code which updates camera position based
         * on half-screen threshold.
         */

        if(mp.isMoving()) {
            switch (mp.getDirection()) {
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

    private void sendUpdatePacket(Player mp) {
        Packet02Move packet = new Packet02Move(mp.getUID(), mp.getUsername(),
                mp.getX(), mp.getY(), mp.isMovingInt(),
                mp.getDirection().getNum(), mp.getType().getNum());
        packet.writeDataFrom(core.getClientThread());
    }

}
