package objects.Tiles;

import objects.Entity;

public class BlockedTile extends Tile {

    public BlockedTile(float x, float y) {
        super(x, y);
    }

    @Override
    public float handleCollision(float px, float py, Entity.Direction dir) {
        switch (dir) {
            case DOWN:
                return getTop();
            case UP:
                return getBottom();
            case LEFT:
                return getRight();
            case RIGHT:
                return getLeft();
            default:
                System.err.printf("Error: Invalid direction");
                return 0;
        }
    }
}
