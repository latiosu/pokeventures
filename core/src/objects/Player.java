package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Entity {

    private PlayerAnimation anim;
    private String username;
    public boolean isMain;

    public Player() {
        this(Type.CHARMANDER, true, "Explorer");
    }

    public Player(Type type, boolean isMain, String username){
        super(type, Direction.DOWN);
        anim = new PlayerAnimation(this, type);
        this.isMain = isMain;
        this.username = username;
    }

    /* Not sure whether best to have Scene2D or own rendering list */
    public void render(float delta, SpriteBatch batch) {
        batch.draw(getFrame(delta), this.x, this.y);
    }

    public void move(Direction d){
        this.setMoving(true);
        this.setDirection(d);
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
}
