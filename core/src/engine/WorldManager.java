package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import objects.Entity;
import objects.PlayerOnline;
import objects.Tiles.BlockedTile;
import objects.Tiles.Tile;
import objects.Tiles.WalkableTile;

import java.util.ArrayList;
import java.util.LinkedList;

public class WorldManager {

    static final int BLACK = Color.rgba8888(0f,0f,0f,1f);
    static final int WHITE = Color.rgba8888(1f,1f,1f,1f);
    static final int RED = Color.rgba8888(1f,0f,0f,1f);
    static final int GREEN = Color.rgba8888(0f,1f,0f,1f);
    static final int BLUE = Color.rgba8888(0f,0f,1f,1f);

    private int tileSize, mapWidth, mapHeight;
    private Texture worldBG;
    private Tile[][] tiles; // Uses i=y, j=x
    private ArrayList<Tile> blocked = new ArrayList<Tile>(); // <--- DEBUGGING USE ONLY

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
        Pixmap pixmap = new Pixmap(Gdx.files.internal(cmap)); /* Warning: Need to optimize this process */
        for (int y = 1; y < mapHeight; y += tileSize) {
            for (int x = 1; x < mapWidth; x += tileSize) { // <--- Start at (1,1) to ignore grid lines

                int colour = pixmap.getPixel(x, y);
                int tx = x-1;
                int ty = mapHeight-y+1-tileSize;

                 /* Note: Try to order by frequency */
                if (colour == WHITE) {
                    t = new WalkableTile(tx, ty);
                } else if (colour == RED) {
                    t = new BlockedTile(tx, ty);
                    blocked.add(t); // DEBUGGING USE ONLY
                } else {
                    t = null;
                    System.err.printf("Error: Could not find colour: %d\n", colour);
                }
                tiles[toTileX(x)][toTileY(mapHeight - y)] = t; // Covert origin to (bottom left) pixel-by-pixel grid
            }
        }

        return tiles;
    }

    /**
     * Searches a 3x3 grid of valid tiles around player for collisions.
     */
    public void handleCollision(PlayerOnline mp) {
        int x = toTileX(mp.getX());
        int y = toTileY(mp.getY());
        for(int j=-1; j<=1; j++) {
            for(int i=-1; i<=1; i++) {
                if(validBlockedTile(x+i, y+j)) {
                    if (Intersector.overlaps(tiles[x+i][y+j].getBounds(), mp.getBounds())) {
                        tiles[x+i][y+j].handleCollision(mp);
                        return;
                    }
                }
            }
        }
    }

    /* DEBUGGING METHOD */
    public ArrayList<Tile> getBlocked() {
        return blocked;
    }

    private boolean validTile(int x, int y) {
        return x >= 0 && y >= 0 && x < mapWidth/tileSize && y < mapHeight/tileSize;
    }

    private boolean validBlockedTile(int x, int y) {
        if(validTile(x, y)) {
            return tiles[x][y] instanceof BlockedTile;
        }
        return false;
    }

    public Tile getTile(float px, float py) {
        return tiles[toTileX(px)][toTileY(py)];
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
