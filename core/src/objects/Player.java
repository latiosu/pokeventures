package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import engine.AssetManager;
import engine.Config;
import engine.Logger;
import engine.components.HealthBar;
import objects.structs.Direction;
import objects.structs.PlayerAnimation;
import objects.structs.PlayerType;

import java.util.Date;

public class Player extends BasePlayer {

    private PlayerAnimation anim;
    private float usernameWidth;
    private HealthBar healthBar;

    /**
     * Used for adding existing players on the server to the clients game.
     * Note: Adds RENDERING components.
     */
    public Player(long uid, float x, float y, Direction dir, PlayerType type, String username,
                  float hp, float maxHp, boolean isAlive) {
        super(uid, x, y, dir, username, type, hp, maxHp, isAlive);
        this.healthBar = new HealthBar(this);
        this.anim = new PlayerAnimation(this, type);
        this.usernameWidth = AssetManager.font.getBounds(username).width;
    }

    /**
     * Used for adding client's own player to game.
     * Note: Adds RENDERING components
     */
    public Player(PlayerType type, String username) {
        super(new Date().getTime(), username, type);
        this.healthBar = new HealthBar(this);
        this.anim = new PlayerAnimation(this, type);
        this.usernameWidth = AssetManager.font.getBounds(username).width;
    }

    /* Note: Using a player list for rendering */
    public void render(float delta, SpriteBatch batch) {
        try {
            // Render sprite
            batch.draw(getFrame(delta), getRenderX(), getRenderY());
            // Render username
            AssetManager.font.draw(batch, this.getUsername(), this.getNameX(), this.getNameY());
            // Render resource bars
            healthBar.draw(batch, 0);
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.log(Logger.Level.ERROR,
                    "Animation not found - %s %s\n",
                    getState().name(),
                    direction.name());
        }
    }

    /**
     * Returns x-coordinate of sprite's rendering origin. Note: This value MAY
     * VARY depending on the character's direction.
     */
    public float getRenderX() {
        switch (direction) {
            case LEFT:
            case RIGHT:
                return x - Config.Rendering.RENDER_OFFSET_X;
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
        return x - (usernameWidth / 2f - (Config.Character.CHAR_WIDTH / 2f));
    }

    /**
     * Returns y-coordinate of a username render origin. Note: This coordinate is
     * automatically adjusted based on username length to appear center-justified.
     */
    public float getNameY() {
        return y + (Config.Character.CHAR_HEIGHT + Config.Rendering.FONT_HEIGHT) + Config.Rendering.USERNAME_PADDING_Y;
    }

    public TextureRegion getFrame(float delta) {
        return anim.getFrame(delta);
    }

    public PlayerAnimation getAnim() {
        return anim;
    }

    /**
     * Note: Will return false if HP is zero.
     */
    public boolean isAlive() {
        return !(hp <= 0 || !isAlive);
    }
}