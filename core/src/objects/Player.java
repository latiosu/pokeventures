package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import engine.AssetManager;
import engine.Config;

public class Player extends Entity {

    private PlayerAnimation anim;
    private String username;
    private float usernameWidth;
    public boolean isMain;

    public Player() {
        this(Type.CHARMANDER, true, "Explorer");
    }

    public Player(Type type, boolean isMain, String username){
        super(type, Direction.DOWN);
        anim = new PlayerAnimation(this, type);
        this.isMain = isMain;
        this.username = username;
        usernameWidth = AssetManager.font.getBounds(username).width;
    }

    /* Using a player list for rendering */
    public void render(float delta, SpriteBatch batch) {
        batch.draw(getFrame(delta), getRenderX(), getRenderY());
//        System.out.printf("%s, %s || %s, %s\n", AssetManager.font.getBounds(username).width,
//                AssetManager.font.getBounds(username).height, getNameX(), getNameY());
        AssetManager.font.draw(batch, username, getNameX(), getNameY());
    }

    public void move(Direction d){
        this.setMoving(true);
        this.setDirection(d);
    }

    /**
     * Returns x-coordinate of collision box origin. Note: The collision box
     * is the user's sensation of the character's physical shape.
     */
    public float getCollisionX() {
        return x + Config.RENDER_OFFSET_X;
    }

    /**
     * Returns y-coordinate of collision box origin. Note: The collision box
     * is the user's sensation of the character's physical shape.
     */
    public float getCollsionY() {
        return y;
    }

    /**
     * Returns x-coordinate of sprite's rendering origin. Note: This value MAY
     * VARY depending on the character's direction.
     */
    public float getRenderX() {
        if(direction == Direction.DOWN || direction == Direction.UP){
            return x + Config.RENDER_OFFSET_X;
        }
        return x;
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
        return x - (usernameWidth/2f - (Config.CHAR_WIDTH/2f)) + Config.RENDER_OFFSET_X;
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
    public void setMoving(int bool) {
        isMoving = bool==1;
    }
    public int isMovingInt() {
        return (isMoving)? 1:0;
    }
}
