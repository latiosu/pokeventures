package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import engine.AssetManager;
import engine.Config;

import java.util.Date;

public class Player extends Entity {

    private long uid;
    private PlayerAnimation anim;
    private String username;
    private float usernameWidth;
    public boolean isMain;

    public Player(String username) {
        this(new Date().getTime(), PlayerType.CHARMANDER, true, username);
    }

    public Player(long uid, PlayerType type, boolean isMain, String username){
        super(type);
        anim = new PlayerAnimation(this, type);
        this.isMain = isMain;
        this.username = username;
        usernameWidth = AssetManager.font.getBounds(username).width;
        this.uid = uid;
    }

    /* Note: Using a player list for rendering */
    public void render(float delta, SpriteBatch batch) {
        batch.draw(getFrame(delta), getRenderX(), getRenderY());
        AssetManager.font.draw(batch, username, getNameX(), getNameY());
    }

    public void move(Direction d){
        this.setMoving(true);
        this.setDirection(d);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, Config.CHAR_COLL_WIDTH, Config.CHAR_COLL_HEIGHT);
    }

    /* DEBUGGING USE ONLY */
    public Vector2 getCentre() {
        return getBounds().getCenter(new Vector2());
    }

    /**
     * Returns x-coordinate of sprite's rendering origin. Note: This value MAY
     * VARY depending on the character's direction.
     */
    public float getRenderX() {
        if(direction == Direction.DOWN || direction == Direction.UP){
            return x;
        }
        return x - Config.RENDER_OFFSET_X;
    }

    /**
     * Returns y-coordinate of sprite's rendering origin. Note: This value MAY
     * VARY depending on the character's direction.
     */
    public float getRenderY() {
        return y;
    }

    /**
     * Returns x-coordinate of a username render origin. Note: This coordinate is
     * automatically adjusted based on username length to appear center-justified.
     */
    public float getNameX() {
        return x - (usernameWidth/2f - (Config.CHAR_WIDTH/2f));
    }

    /**
     * Returns y-coordinate of a username render origin. Note: This coordinate is
     * automatically adjusted based on username length to appear center-justified.
     */
    public float getNameY() {
        return y + (Config.CHAR_HEIGHT + Config.FONT_HEIGHT) + Config.USERNAME_PADDING_Y;
    }
    public TextureRegion getFrame(float delta){
        return anim.getFrame(delta);
    }
    public PlayerAnimation getAnim() {
        return anim;
    }
    public void setUsername(String name) {
        username = name;
    }
    public String getUsername() {
        return username;
    }
    public void setMoving(boolean b) {
        isMoving = b;
    }
    public int isMovingNum() {
        return (isMoving)? 1:0;
    }
    public long getUID() {
        return uid;
    }
}