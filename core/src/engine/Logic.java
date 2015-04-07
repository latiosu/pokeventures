package engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import networking.packets.Packet02Move;
import objects.Entity;
import objects.Player;

public class Logic {

    private Core core;
    private OrthographicCamera cam;

    private float mapWidth, mapHeight, minCamX, minCamY, maxCamX, maxCamY,
            spawnX, spawnY, minX, minY, maxX, maxY;

    public Logic(Core core, OrthographicCamera cam){
        this.core = core;
        this.cam = cam;

        mapWidth = AssetManager.level.getWidth();
        mapHeight = AssetManager.level.getHeight();
        spawnX = Config.SPAWN_X;
        spawnY = Config.SPAWN_Y;
        minCamX = Config.CAM_MIN_X;
        minCamY = Config.CAM_MIN_Y;

        // Not in config
        minX = 0;
        minY = 0;
        maxX = mapWidth - Config.CHAR_WIDTH;
        maxY = mapHeight - Config.CHAR_HEIGHT;
        maxCamX = mapWidth - (Config.VIEWPORT_WIDTH/2f);
        maxCamY = mapHeight - (Config.VIEWPORT_HEIGHT/2f);
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
        mp.setMoving(true);
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
                    if(mp.getY() > minY) {
                        mp.setY(mp.getY() - Config.WALK_DIST);
                    }
                    break;
                case LEFT:
                    if(mp.getX() > minX) {
                        mp.setX(mp.getX() - Config.WALK_DIST);
                    }
                    break;
                case UP:
                    if(mp.getY() < maxY) {
                        mp.setY(mp.getY() + Config.WALK_DIST);
                    }
                    break;
                case RIGHT:
                    if(mp.getX() < maxX) {
                        mp.setX(mp.getX() + Config.WALK_DIST);
                    }
                    break;
            }
        }

        // Send movement packet with freshly updated main player data
        sendUpdatePacket(mp);
    }

    /**
     * Camera algorithm only follows player if camera can be centered.
     */
    private void updateCamera(Player mp) {
//        System.out.printf("%.0f, %.0f\n", mp.getX(), mp.getY());
//        if(mp.isMoving()) {
        cam.position.x = mp.getX();
        cam.position.y = mp.getY();
        cam.update();
        updateCameraBounds();
//        }
    }

    private void updateCameraBounds() {
        // Map boundaries
        if(cam.position.x > maxCamX) {
            cam.position.x = maxCamX;
        }
        if(cam.position.y > maxCamY) {
            cam.position.y = maxCamY;
        }
        if(cam.position.x < minCamX) {
            cam.position.x = minCamX;
        }
        if(cam.position.y < minCamY) {
            cam.position.y = minCamY;
        }
        cam.update();
    }

    private void sendUpdatePacket(Player mp) {
        Packet02Move packet = new Packet02Move(mp.getUID(), mp.getUsername(),
                mp.getX(), mp.getY(), mp.isMovingNum(), mp.getDirection().getNum(), mp.getType().getNum());
        packet.writeDataFrom(core.getClientThread());
    }

}
