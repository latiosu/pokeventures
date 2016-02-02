package objects.Tiles;

import engine.Logger;
import objects.Player;

public class BlockedTile extends Tile {

    public BlockedTile(float x, float y) {
        super(x, y);
    }

    @Override
    public void resolveCollision(Player mp) {
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
                Logger.log(Logger.Level.ERROR,
                        "Invalid direction (%s)\n",
                        mp.getDirection());
                break;
        }
    }
}
