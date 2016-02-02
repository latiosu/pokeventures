package objects.Tiles;

import engine.Logger;
import objects.Player;
import objects.structs.Direction;

public class JumpableTile extends BlockedTile {

    private Direction validDir;

    /**
     * This tile is only traversable in one validDir (specified by parameter).
     */
    public JumpableTile(float x, float y, Direction validDir) {
        super(x, y);
        this.validDir = validDir;
    }

    @Override
    public void resolveCollision(Player mp) {
        boolean resolved = false;
        switch (validDir) {
            case DOWN:
                if (mp.getY() > y) {
                    mp.getAnim().triggerJump(validDir);
                    resolved = true;
                }
                break;
            case UP:
                if (mp.getY() < y) {
                    mp.getAnim().triggerJump(validDir);
                    resolved = true;
                }
                break;
            case LEFT:
                if (mp.getX() > x) {
                    mp.getAnim().triggerJump(validDir);
                    resolved = true;
                }
                break;
            case RIGHT:
                if (mp.getX() < x) {
                    mp.getAnim().triggerJump(validDir);
                    resolved = true;
                }
                break;
            default:
                Logger.log(Logger.Level.ERROR,
                        "Not a valid JumpableTile direction (%s)\n",
                        validDir);
                break;
        }
        // Copy behaviour of blocked tile if not yet resolved
        if (!resolved) {
            super.resolveCollision(mp);
        }
    }

    public Direction getValidDir() {
        return validDir;
    }
}
