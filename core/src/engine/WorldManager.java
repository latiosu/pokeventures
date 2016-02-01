package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import objects.Player;
import objects.Tiles.BlockedTile;
import objects.Tiles.JumpableTile;
import objects.Tiles.Tile;
import objects.Tiles.WalkableTile;
import objects.structs.Direction;

import java.util.ArrayList;
import java.util.List;

public class WorldManager {

//    static final int BLACK = Color.rgba8888(0f, 0f, 0f, 1f);
    static final int WHITE = Color.rgba8888(1f, 1f, 1f, 1f); // Walkable Tile
    static final int RED = Color.rgba8888(1f, 0f, 0f, 1f); // Blocked Tile
    static final int GREEN = Color.rgba8888(0f, 1f, 0f, 1f); // Down-jump Tile
    static final int BLUE = Color.rgba8888(0f, 0f, 1f, 1f); // Left-jump Tile

    private int tileSize, mapWidth, mapHeight;
    private Tile[][] tiles; // Uses i=y, j=x
    private List<Tile> blocked;
    private List<JumpableTile> jumpable;

    public WorldManager() {
        blocked = new ArrayList<>();
        jumpable = new ArrayList<>();
    }

    public Texture loadWorld(String map) {
        Texture worldBG = AssetManager.level;
        tileSize = Config.World.TILE_SIZE;
        mapWidth = worldBG.getWidth();
        mapHeight = worldBG.getHeight();
        tiles = parseWorld(Config.ASSETS_PATH + "collision-" + map);
        return worldBG;
    }

    /**
     * Note: The map should be fully divisible by the tile size.
     */
    private Tile[][] parseWorld(String collisionMap) {
        Tile[][] tiles = new Tile[mapWidth / tileSize][mapHeight / tileSize];
        Tile t;
        Pixmap pixmap = new Pixmap(Gdx.files.internal(collisionMap)); /* Warning: Need to optimize this process */
        for (int y = 1; y < mapHeight; y += tileSize) {
            for (int x = 1; x < mapWidth; x += tileSize) { // <--- Start at (1,1) to ignore grid lines

                int colour = pixmap.getPixel(x, y);
                int tx = x - 1;
                int ty = mapHeight - y + 1 - tileSize;

                 /* Note: Try to order by frequency */
                if (colour == WHITE) {
                    t = new WalkableTile(tx, ty);
                } else if (colour == RED) {
                    t = new BlockedTile(tx, ty);
                    if (Config.DEBUG) {
                        blocked.add(t);
                    }
                } else if (colour == GREEN) {
                    t = new JumpableTile(tx, ty, Direction.DOWN);
                    if (Config.DEBUG) {
                        jumpable.add((JumpableTile) t);
                    }
                } else if (colour == BLUE) {
                    t = new JumpableTile(tx, ty, Direction.LEFT);
                    if (Config.DEBUG) {
                        jumpable.add((JumpableTile) t);
                    }
                } else {
                    t = null;
                    Logger.log(Logger.Level.ERROR,
                            "Could not find colour: %d\n",
                            colour);
                }
                tiles[toTileX(x)][toTileY(mapHeight - y)] = t; // Covert origin to (bottom left) pixel-by-pixel grid
            }
        }

        return tiles;
    }

    /**
     * Searches a 3x3 grid of valid Tiles around player for collisions.
     */
    public Tile handleCollision(Player mp) {
        int x = toTileX(mp.getX());
        int y = toTileY(mp.getY());
        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                if (validTile(x + i, y + j) && tiles[x + i][y + j] instanceof BlockedTile) {
                    if (Intersector.overlaps(tiles[x + i][y + j].getBounds(), mp.getBounds())) {
                        tiles[x + i][y + j].resolveCollision(mp);
                        return tiles[x + i][y + j];
                    }
                }
            }
        }
        return null;
    }

    /**
     * For debugging use only.
     *
     * @return - list of tiles which players cannot pass through.
     * */
    public List<Tile> getBlocked() {
        return blocked;
    }

    /**
     * For debugging use only.
     *
     * @return - list of tiles which player can jump over.
     */
    public List<JumpableTile> getJumpable() {
        return jumpable;
    }

    /**
     * Returns tile at given player coordinates.
     * Note: Does not validate coordinates given.
     *
     * @param px - player's x coordinate
     * @param py - player's y coordinate
     * @return - tile at given coordinates
     */
    public Tile getTile(float px, float py) {
        return tiles[toTileX(px)][toTileY(py)];
    }

    private boolean validTile(int x, int y) {
        return x >= 0 && y >= 0 && x < mapWidth / tileSize && y < mapHeight / tileSize;
    }

    /**
     * Converts given player x-coordinate to respective tile map x-coordinate.
     */
    private int toTileX(float px) {
        return (int) (px / tileSize);
    }

    /**
     * Converts given player y-coordinate to respective tile map y-coordinate.
     */
    private int toTileY(float py) {
        return (int) (py / tileSize);
    }
}
