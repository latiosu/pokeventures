package engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import networking.packets.Packet02Move;
import objects.Direction;
import objects.Player;
import objects.PlayerOnline;
import objects.State;

public class Logic {

    private Core core;
    private OrthographicCamera cam;

    public Logic(Core core, OrthographicCamera cam) {
        this.core = core;
        this.cam = cam;
    }

    /**
     * Note: Client handles collisions, positioning and rendering of main player.
     */
    public void update(PlayerOnline mp) {
        updateMainPlayer(mp);
        updateCamera(mp);
    }

    private void updateMainPlayer(PlayerOnline mp) {
        // Input logic
        boolean[] arrows = UserInputProcessor.directionKeys;
        boolean[] attacks = UserInputProcessor.attackKeys;
        mp.setType(UserInputProcessor.selectedType);

        // Update animation state
        if (attacks[0]) {
            mp.setState(State.ATK_MELEE);
        } else if (attacks[1]) {
            mp.setState(State.ATK_RANGED);
        } else if (!arrows[0] && !arrows[1] && !arrows[2] && !arrows[3]) {
            mp.setState(State.IDLE);
        } else {
            mp.setState(State.WALK);
        }

        // Attack / Movement logic
        if (mp.getState() == State.ATK_MELEE) {
            System.out.println("Attack: MELEE");
        } else if (mp.getState() == State.ATK_RANGED) {
            System.out.println("Attack: RANGED");
        } else if (mp.getState() == State.WALK) { // Note: Note able to walk + attack simultaneously
            // Update movement direction flags only during walk state
            if (arrows[0]) {
                mp.setDirection(Direction.DOWN);
            } else if (arrows[1]) {
                mp.setDirection(Direction.LEFT);
            } else if (arrows[2]) {
                mp.setDirection(Direction.UP);
            } else if (arrows[3]) {
                mp.setDirection(Direction.RIGHT);
            }
            switch (mp.getDirection()) {
                case DOWN:
                    if (mp.getY() > 0) {
                        mp.setY(mp.getY() - Config.WALK_DIST);
                    }
                    break;
                case LEFT:
                    if (mp.getX() > 0) {
                        mp.setX(mp.getX() - Config.WALK_DIST);
                    }
                    break;
                case UP:
                    if (mp.getY() < AssetManager.level.getHeight() - Config.CHAR_COLL_HEIGHT) {
                        mp.setY(mp.getY() + Config.WALK_DIST);
                    }
                    break;
                case RIGHT:
                    if (mp.getX() < AssetManager.level.getWidth() - Config.CHAR_COLL_WIDTH) {
                        mp.setX(mp.getX() + Config.WALK_DIST);
                    }
                    break;
            }
        }

        // UPDATE PLAYER ANIMATION IMMEDIATELY FOR RESPONSIVENESS
        mp.getAnim().updateAnim();

        // Handle collision for projectiles
//        core.getWorldManager().handleProjectiles();

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
        if (cam.position.x > Config.CAM_MAX_X) {
            cam.position.x = Config.CAM_MAX_X;
        }
        if (cam.position.y > Config.CAM_MAX_Y) {
            cam.position.y = Config.CAM_MAX_Y;
        }
        if (cam.position.x < Config.CAM_MIN_X) {
            cam.position.x = Config.CAM_MIN_X;
        }
        if (cam.position.y < Config.CAM_MIN_Y) {
            cam.position.y = Config.CAM_MIN_Y;
        }
        cam.update();
    }

    private void sendUpdatePacket(Player mp) {
        Packet02Move packet = new Packet02Move(mp.getUID(), mp.getUsername(),
                mp.getX(), mp.getY(), mp.getState().getNum(),
                mp.getDirection().getNum(), mp.getType().getNum());
        packet.writeDataFrom(core.getClientThread());
    }

}
