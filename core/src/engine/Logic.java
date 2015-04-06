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
        minX = spawnX;
        minY = spawnY;
        maxX = mapWidth + spawnX - Config.CHAR_WIDTH;
        maxY = mapHeight + spawnY - Config.CHAR_HEIGHT;
        maxCamX = mapWidth + spawnX - (Config.VIEWPORT_WIDTH/2f);
        maxCamY = mapHeight + spawnY - (Config.VIEWPORT_HEIGHT/2f);
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

        // Send movement packet to server with integer substitutions
        sendUpdatePacket(mp);
    }

    /**
     * Camera algorithm only follows player if camera can be centered.
     */
    private void updateCamera(Player mp) {
        if(mp.isMoving()) {
            switch (mp.getDirection()) {
                case DOWN:
                    if(mp.getY() < maxCamY) {
                        cam.translate(0, -Config.WALK_DIST);
                    }
                    break;
                case LEFT:
                    if(mp.getX() < maxCamX) {
                        cam.translate(-Config.WALK_DIST, 0);
                    }
                    break;
                case UP:
                    if(mp.getY() > minCamY) {
                        cam.translate(0, Config.WALK_DIST);
                    }
                    break;
                case RIGHT:
                    if(mp.getX() > minCamX) {
                        cam.translate(Config.WALK_DIST, 0);
                    }
                    break;
            }
            updateCameraBounds();
            cam.update();
        }
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
//        System.out.printf("XY(%.0f,%.0f) MIN(%.0f,%.0f) MAX(%.0f,%.0f)\n", mp.getX(), mp.getY(), minCamX, minCamY, maxCamX, maxCamY);
//        System.out.printf("XY=(%.1f,%.1f) Cam=(%.1f,%.1f) Map=(%.1f,%.1f)) Spawn=(%.1f,%.1f) VP=(%.1f,%.1f)\n",
//                core.getPlayers().getMainPlayer().getX(), core.getPlayers().getMainPlayer().getY(),
//                cam.position.x, cam.position.y, mapWidth, mapHeight,
//                Config.SPAWN_X, Config.SPAWN_Y, Config.VIEWPORT_WIDTH, Config.VIEWPORT_HEIGHT);
//        System.out.printf("%.1f, %.1f %.1f\n", cam.position.x, maxCamX, mapWidth+Config.SPAWN_X-(Config.VIEWPORT_WIDTH/2f));
    }

    private void sendUpdatePacket(Player mp) {
        Packet02Move packet = new Packet02Move(mp.getUID(), mp.getUsername(),
                mp.getX(), mp.getY(), mp.isMovingInt(),
                mp.getDirection().getNum(), mp.getType().getNum());
        packet.writeDataFrom(core.getClientThread());
    }

}
