package objects.Tiles;

import objects.PlayerOnline;

public class WalkableTile extends Tile {

    public WalkableTile(float x, float y) {
        super(x, y);
    }

    @Override
    /**
     * Ignores collisions.
     */
    public void handleCollision(PlayerOnline mp) {
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
                System.err.printf("Error: Invalid direction");
                break;
        }
    }
}
