package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import objects.Entity;
import objects.PlayerOnline;
import objects.Tiles.BlockedTile;
import objects.Tiles.Tile;
import objects.Tiles.WalkableTile;

public class WorldManager {

    static final int BLACK = Color.rgba8888(0f,0f,0f,1f);
    static final int WHITE = Color.rgba8888(1f,1f,1f,1f);
    static final int RED = Color.rgba8888(1f,0f,0f,1f);
    static final int GREEN = Color.rgba8888(0f,1f,0f,1f);
    static final int BLUE = Color.rgba8888(0f,0f,1f,1f);

    private int tileSize, mapWidth, mapHeight;
    private Texture worldBG;
    private Tile[][] tiles; // Uses i=y, j=x

    public WorldManager() {

    }

    public Texture loadWorld(String map) {
        worldBG = AssetManager.level;
        tileSize = Config.TILE_SIZE;
        mapWidth = worldBG.getWidth();
        mapHeight = worldBG.getHeight();
        tiles = parseWorld("assets/collision-" + map);
        return worldBG;
    }

    /**
     * Note: The map should be fully divisible by the tile size.
     */
    private Tile[][] parseWorld(String cmap) {
        Tile[][] tiles = new Tile[mapWidth / tileSize][mapHeight / tileSize];
        Tile t = null;
        int red = 0, white = 0, black = 0;

        Pixmap pixmap = new Pixmap(Gdx.files.internal(cmap)); /* Warning: Need to optimize this process */
        for (int y = 1; y < mapHeight; y += tileSize) {
            for (int x = 1; x < mapWidth; x += tileSize) { // <--- Start at (1,1) to ignore grid lines

                int colour = pixmap.getPixel(x, y);
                int tx = x-1;
                int ty = mapHeight-y-1;

                 /* Make sure to order by frequency */
                if (colour == WHITE) {
                    t = new WalkableTile(tx, ty);
                    white++;
                } else if (colour == RED) {
                    t = new BlockedTile(tx, ty);
                    red++;
                } else if (colour == BLACK) {
                    black++;
                } else if (colour == GREEN) {

                } else if (colour == BLUE) {

                } else {
                    t = null;
                    System.err.printf("Error: Could not find colour: %d", colour);
                }
                tiles[x / tileSize][(mapHeight - y) / tileSize] = t; // Covert origin to (bottom left) pixel-by-pixel grid
            }
        }

        System.out.printf("red=%d, white=%d, black=%d\n", red, white, black);

        return tiles;
    }

    public void handleCollision(PlayerOnline p, float px, float py, Entity.Direction dir) {
        float newPos = tiles[toTileX(px)][toTileY(py)].handleCollision(px, py, dir);
//        System.out.printf("%d %d %f %s\n", toTileX(x), toTileY(y), newPos, tiles[toTileX(x)][toTileY(y)].getClass().getName());

        switch (dir) {
            case UP:
            case DOWN:
                p.setY(newPos);
                break;
            case LEFT:
            case RIGHT:
                p.setX(newPos);
                break;
        }
    }

    public Tile getTile(float px, float py) {
        return tiles[toTileX(px)][toTileY(py)];
    }

    /**
     * Converts given player x-coordinate to respective tile map x-coordinate.
     */
    private int toTileX(float px) {
        return (int)(px/tileSize); //return (int)((px + Config.SPAWN_X)/tileSize);
    }
    /**
     * Converts given player y-coordinate to respective tile map y-coordinate.
     */
    private int toTileY(float py) {
        return (int)(py/tileSize); // return (int)((py + Config.SPAWN_Y)/tileSize);
    }
}
