package objects;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class Player {

    private final float WALK_DIST = 5f;

    private float x, y;
    private Type t;
    private PlayerAnimation anim;
    private OrthographicCamera cam;

    public Player(Type t, PlayerAnimation anim, OrthographicCamera cam){
        this.t = t;
        this.anim = anim;
        this.cam = cam;
        x = 0;
        y = 0;
    }

    public void move(Direction d){
        anim.play(t,d);
        switch (d){
            case DOWN:
                y-=WALK_DIST;
                cam.translate(0, -WALK_DIST);
                break;
            case LEFT:
                x-=WALK_DIST;
                cam.translate(-WALK_DIST, 0);
                break;
            case UP:
                y+=WALK_DIST;
                cam.translate(0, WALK_DIST);
                break;
            case RIGHT:
                x+=WALK_DIST;
                cam.translate(WALK_DIST, 0);
                break;
        }
        cam.update();
    }

    public void changePlayer(Type t){
        this.t = t;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
