package engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import networking.packets.Packet02Move;
import objects.Direction;
import objects.Player;
import objects.PlayerOnline;

public class Logic {

    private Core core;
    private OrthographicCamera cam;

    public Logic(Core core, OrthographicCamera cam){
        this.core = core;
        this.cam = cam;
    }

    /**
     * Note: Client handles collisions, positioning and rendering of main player.
     */
    public void update(PlayerOnline mp){
        updateMainPlayer(mp);
        updateCamera(mp);
    }

    private void updateMainPlayer(PlayerOnline mp) {
        // Input logic
        boolean[] keys = UserInputProcessor.directionKeys;
        mp.setType(UserInputProcessor.selectedType);
        mp.setMoving(true);
        if(!keys[0] && !keys[1] && !keys[2] && !keys[3]) {
            mp.setMoving(false);
        } else if (keys[0]) {
            mp.move(Direction.DOWN);
        } else if (keys[1]) {
            mp.move(Direction.LEFT);
        } else if (keys[2]) {
            mp.move(Direction.UP);
        } else if (keys[3]) {
            mp.move(Direction.RIGHT);
        }

        // Position logic
        if(mp.isMoving()) {
            switch (mp.getDirection()) {
                case DOWN:
                    if(mp.getY() > 0) {
                        mp.setY(mp.getY() - Config.WALK_DIST);
                    }
                    break;
                case LEFT:
                    if(mp.getX() > 0) {
                        mp.setX(mp.getX() - Config.WALK_DIST);
                    }
                    break;
                case UP:
                    if(mp.getY() < AssetManager.level.getHeight() - Config.CHAR_COLL_HEIGHT) {
                        mp.setY(mp.getY() + Config.WALK_DIST);
                    }
                    break;
                case RIGHT:
                    if(mp.getX() < AssetManager.level.getWidth() - Config.CHAR_COLL_WIDTH) {
                        mp.setX(mp.getX() + Config.WALK_DIST);
                    }
                    break;
            }
        }

        // Handle collision for main player
        core.getWorldManager().handleCollision(mp);

        // Send movement packet with freshly updated main player data
        sendUpdatePacket(mp);
    }


    private void updateCamera(Player mp) {
        cam.position.x = mp.getX();
        cam.position.y = mp.getY();
        cam.update();
        updateCameraBounds();
    }

    /**
     * Camera algorithm only follows player if camera can be centered.
     */
    private void updateCameraBounds() {
        // Map boundaries
        if(cam.position.x > Config.CAM_MAX_X) {
            cam.position.x = Config.CAM_MAX_X;
        }
        if(cam.position.y > Config.CAM_MAX_Y) {
            cam.position.y = Config.CAM_MAX_Y;
        }
        if(cam.position.x < Config.CAM_MIN_X) {
            cam.position.x = Config.CAM_MIN_X;
        }
        if(cam.position.y < Config.CAM_MIN_Y) {
            cam.position.y = Config.CAM_MIN_Y;
        }
        cam.update();
    }

    private void sendUpdatePacket(Player mp) {
        Packet02Move packet = new Packet02Move(mp.getUID(), mp.getUsername(),
                mp.getX(), mp.getY(), mp.isMovingNum(), mp.getDirection().getNum(), mp.getType().getNum());
        packet.writeDataFrom(core.getClientThread());
    }

}
