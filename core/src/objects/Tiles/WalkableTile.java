package objects.Tiles;

import objects.Entity;

public class WalkableTile extends Tile {

    public WalkableTile(float x, float y) {
        super(x, y);
    }

    @Override
    public float handleCollision(float px, float py, Entity.Direction dir) {
        switch (dir) {
            case DOWN:
            case UP:
                return py;
            case LEFT:
            case RIGHT:
                return px;
            default:
                System.err.printf("Error: Invalid direction");
                return 0;
        }
    }
}
