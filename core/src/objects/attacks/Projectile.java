package objects.attacks;

import com.badlogic.gdx.math.Rectangle;
import objects.Direction;
import objects.GameObject;

public class Projectile extends GameObject {

    private int width, height;

    public Projectile(Direction d, float x, float y, int w, int h) {
        super(d, x, y);
        this.width = w;
        this.height = h;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

}
