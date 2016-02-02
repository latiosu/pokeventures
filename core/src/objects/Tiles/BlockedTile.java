package objects.Tiles;

import objects.Player;

public class BlockedTile extends Tile {

    public BlockedTile(float x, float y) {
        super(x, y);
    }

    @Override
    public void handleCollision(Player mp) {
        switch (mp.getDirection()) {
            case DOWN:
                mp.setY(getTop());
                break;
            case UP:
                mp.setY(getBottom());
                break;
            case LEFT:
                mp.setX(getRight());
                break;
            case RIGHT:
                mp.setX(getLeft());
                break;
            default:
                System.err.printf("Error: Invalid direction");
                break;
        }
    }
}
