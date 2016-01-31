package objects.Tiles;

import engine.Logger;
import objects.Player;

public class WalkableTile extends Tile {
    /**
     * Ignores collisions.
     */
    public WalkableTile(float x, float y) {
        super(x, y);
    }

    @Override
    public void handleCollision(Player mp) {
        switch (mp.getDirection()) {
            case DOWN:
            case UP:
                mp.getX();
                break;
            case LEFT:
            case RIGHT:
                mp.getY();
                break;
            default:
                Logger.log(Logger.Level.ERROR,
                        "Invalid direction (%s)\n",
                        mp.getDirection());
                break;
        }
    }
}
