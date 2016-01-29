package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import engine.AssetManager;
import engine.Config;

public class Player extends Entity {

    private long uid;
    private PlayerAnimation anim;
    private String username;
    private float usernameWidth;
    private float hp;
    private float maxHp;
    private long lastAttackTime;
    private boolean isAlive;

    public Player(long uid, PlayerType type, String username) {
        super(type);
        this.uid = uid;
        this.anim = new PlayerAnimation(this, type);
        this.username = username;
        this.usernameWidth = AssetManager.font.getBounds(username).width;
        this.hp = Config.PLAYER_HP;
        this.maxHp = Config.PLAYER_HP;
        this.lastAttackTime = 0;
        this.isAlive = true;
    }

    /* Note: Using a player list for rendering */
    public void render(float delta, SpriteBatch batch) {
        try {
            batch.draw(getFrame(delta), getRenderX(), getRenderY());
            AssetManager.font.draw(batch, username, getNameX(), getNameY());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.printf("Error: Animation not found - %s %s\n", state.name(), direction.name());
        }
    }

    public void updateDamage(Attack atk) {
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

    /**
     * Returns x-coordinate of sprite's rendering origin. Note: This value MAY
     * VARY depending on the character's direction.
     */
    public float getRenderX() {
        switch (direction) {
            case LEFT:
            case RIGHT:
                return x - Config.RENDER_OFFSET_X;
            default:
                return x;
        }
    }

    /**
     * Returns y-coordinate of sprite's rendering origin. Note: This value MAY
     * VARY depending on the character's direction.
     */
    public float getRenderY() {
        return y - 1;
    }

    /**
     * Returns x-coordinate of a username render origin. Note: This coordinate is
     * automatically adjusted based on username length to appear center-justified.
     */
    public float getNameX() {
        return x - (usernameWidth / 2f - (Config.CHAR_WIDTH / 2f));
    }

    /**
     * Returns y-coordinate of a username render origin. Note: This coordinate is
     * automatically adjusted based on username length to appear center-justified.
     */
    public float getNameY() {
        return y + (Config.CHAR_HEIGHT + Config.FONT_HEIGHT) + Config.USERNAME_PADDING_Y;
    }

    public TextureRegion getFrame(float delta) {
        return anim.getFrame(delta);
    }

    public PlayerAnimation getAnim() {
        return anim;
    }

    public String getUsername() {
        return username;
    }

    public long getUID() {
        return uid;
    }

    public float getHp() {
        return hp;
    }

    public void setHp(float hp) {
        this.hp = hp;
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

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}