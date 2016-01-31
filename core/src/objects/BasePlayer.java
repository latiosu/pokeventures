package objects;

import com.badlogic.gdx.math.Rectangle;
import engine.Config;
import objects.structs.Direction;
import objects.structs.PlayerType;

import java.net.InetAddress;

public class BasePlayer extends Entity {

    protected InetAddress address;
    protected int port;
    protected long uid;
    protected String username;
    protected float hp, maxHp;

    private long lastAttackTime; // TODO: Refactor to elsewhere (Maybe InputProcessor)

    /**
     * Used for new players joining the server. Default spawn configuration.
     */
    public BasePlayer(long uid, String username, PlayerType type) {
        this(uid,
                Config.SPAWN_X,
                Config.SPAWN_Y,
                Direction.DOWN,
                username,
                type,
                Config.PLAYER_MAX_HP,
                Config.PLAYER_MAX_HP,
                true);
    }

    /**
     * Used by other constructors to initialize values of the player.
     */
    public BasePlayer(long uid, float x, float y, Direction dir, String username, PlayerType type,
                      float hp, float maxHp, boolean isAlive) {
        super(type);
        this.uid = uid;
        this.x = x;
        this.y = y;
        this.direction = dir;
        this.username = username;
        this.hp = hp;
        this.maxHp = maxHp;
        this.isAlive = isAlive;
        this.address = null;
        this.port = -1;

        this.lastAttackTime = 0;
    }

    public void updateDamage(BaseAttack atk) {
        if (isAlive()) {
            setHp(getHp() - atk.getDamage());
            if (getHp() <= 0) {
                this.setAlive(false);
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, Config.CHAR_COLL_WIDTH, Config.CHAR_COLL_HEIGHT);
    }

    public String getUsername() {
        return username;
    }

    public long getUid() {
        return uid;
    }

    public float getHp() {
        return hp;
    }

    public void setHp(float hp) {
        if (hp < 0) {
            this.hp = 0;
        } else {
            this.hp = hp;
        }
    }

    public float getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void updateLastAttackTime() {
        this.lastAttackTime = System.nanoTime();
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
