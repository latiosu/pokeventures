package engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import networking.packets.Packet02Move;
import networking.packets.Packet04Attack;
import objects.Direction;
import objects.Player;
import objects.PlayerOnline;
import objects.State;
import objects.attacks.Attack;
import objects.attacks.AttackType;
import objects.attacks.MeleeAttack;
import objects.attacks.RangedAttack;

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
        updateAttacks(mp);
        updateCamera(mp);
    }

    private void updateMainPlayer(Player mp) {
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
            generateAttack(mp, AttackType.MELEE);
        } else if (mp.getState() == State.ATK_RANGED) {
            generateAttack(mp, AttackType.RANGED);
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

        // Update player animations immediately for responsiveness
        mp.getAnim().updateAnim();
        // <----- Play sounds immediately for responsiveness in the future

        // Handle collision for main player
        core.getWorldManager().handleCollision(mp);

        // Send movement packet with freshly updated main player data
        sendUpdatePacket(mp);
    }

    /**
     * Attempts send a packet to server containing data of newly generated attack.
     */
    private void generateAttack(Player mp, AttackType type) {
        Attack atk = null;
        switch (type) {
            case MELEE:
                if (!mp.hasMeleeAtk()) {
                    atk = new MeleeAttack(mp.getUID(), mp.getType(), mp.getDirection(), mp.getX(), mp.getY());
                    mp.setHasMeleeAtk(true);
                }
                break;
            case RANGED:
                if (!mp.hasRangedAtk()) {
                    atk = new RangedAttack(mp.getUID(), mp.getType(), mp.getDirection(), mp.getX(), mp.getY());
                    mp.setHasRangedAtk(true);
                }
                break;
            default:
                System.err.printf("Error: Attack not found - %s", type.name());
        }
        if (atk != null) {
            core.getAttacks().add(atk.getId(), atk);
            sendAttackPacket(atk);
        }
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

    /**
     * Each client checks for collisions with their own character and all existing projectiles.
     * Note: *Need to optimize with quadtree*
     */
    private void updateAttacks(Player mp) {
        for (Attack a : core.getAttacks()) {
            if (a.update(mp)) {
                /* Send a packet if collision is detected */
                sendAttackPacket(a);
            }
        }
    }

    private void sendUpdatePacket(Player mp) {
        Packet02Move pk = new Packet02Move(mp.getUID(), mp.getUsername(),
                mp.getX(), mp.getY(), mp.getState().getNum(),
                mp.getDirection().getNum(), mp.getType().getNum());
        pk.writeDataFrom(core.getClientThread());
    }

    private void sendAttackPacket(Attack atk) {
        Packet04Attack pk = new Packet04Attack(atk.getId(),  atk.getUid(), atk.getPtype().getNum(),
                atk.getDirection().getNum(), atk.getX(), atk.getY(), atk.getAtkType().getNum(), atk.isAliveNum());
        pk.writeDataFrom(core.getClientThread());
    }

}
