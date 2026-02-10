package io.SesProject.model.game.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * Main data structure for holding map information.
 * Manages multiple layers in rendering order.
 */
public class GameMap {

    private List<Layer> layers;
    private String mapName;
    private int mapWidthInTiles;
    private int mapHeightInTiles;
    private int tileWidth;
    private int tileHeight;

    // Default map dimensions for fallback
    private static final int DEFAULT_MAP_WIDTH_PIXELS = 640;
    private static final int DEFAULT_MAP_HEIGHT_PIXELS = 480;

    public GameMap(String mapName) {
        this.mapName = mapName;
        this.layers = new ArrayList<>();
        this.mapWidthInTiles = 0;
        this.mapHeightInTiles = 0;
        this.tileWidth = 32;  // Default value
        this.tileHeight = 32;
    }

    /**
     * Sets the dimensions of the map
     */
    public void setDimensions(int widthInTiles, int heightInTiles, int tileWidth, int tileHeight) {
        this.mapWidthInTiles = widthInTiles;
        this.mapHeightInTiles = heightInTiles;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    /**
     * Gets the width of the map in pixels
     */
    public int getMapWidthPixels() {
        if (mapWidthInTiles == 0) {
            System.err.println("[GameMap] Warning: Map dimensions a not set, returning default");
            return DEFAULT_MAP_WIDTH_PIXELS;
        }
        return mapWidthInTiles * tileWidth;
    }

    /**
     * Gets the height of the map in pixels
     */
    public int getMapHeightPixels() {
        if (mapHeightInTiles == 0) {
            System.err.println("[GameMap] Warning: Map dimensions b not set, returning default");
            return DEFAULT_MAP_HEIGHT_PIXELS;
        }
        return mapHeightInTiles * tileHeight;
    }

    /**
     * Adds a layer to the map
     */
    public void addLayer(Layer layer) {
        layers.add(layer);
        // Sort layers by z-order to ensure proper rendering
        layers.sort((l1, l2) -> Integer.compare(l1.getZOrder(), l2.getZOrder()));
    }

    /**
     * Gets a layer by name
     */
    public Layer getLayer(String name) {
        for (Layer layer : layers) {
            if (layer.getName().equals(name)) {
                return layer;
            }
        }
        return null;
    }

    /**
     * Gets all layers
     */
    public List<Layer> getLayers() {
        return layers;
    }

    /**
     * Renders all layers in order
     */
    public void render(SpriteBatch batch) {
        for (Layer layer : layers) {
            layer.render(batch);
        }
    }

    /**
     * Gets all solid tiles for collision detection
     */
    public List<Tile> getSolidTiles() {
        List<Tile> solidTiles = new ArrayList<>();
        for (Layer layer : layers) {
            collectSolidTiles(layer, solidTiles);
        }
        return solidTiles;
    }

    private void collectSolidTiles(MapComponent component, List<Tile> result) {
        if (component instanceof Tile) {
            Tile tile = (Tile) component;
            if (tile.isSolid()) {
                result.add(tile);
            }
        }

        for (MapComponent child : component.getChildren()) {
            collectSolidTiles(child, result);
        }
    }

    /**
     * Gets all transition tiles for map transitions
     */
    public List<Tile> getTransitionTiles() {
        List<Tile> transitionTiles = new ArrayList<>();
        for (Layer layer : layers) {
            collectTransitionTiles(layer, transitionTiles);
        }
        return transitionTiles;
    }

    private void collectTransitionTiles(MapComponent component, List<Tile> result) {
        if (component instanceof Tile) {
            Tile tile = (Tile) component;
            if (tile.isTransition()) {
                result.add(tile);
            }
        }

        for (MapComponent child : component.getChildren()) {
            collectTransitionTiles(child, result);
        }
    }

    /**
     * Gets all spawn tiles (tiles with spawnType property set)
     */
    public List<Tile> getSpawnTiles() {
        List<Tile> spawnTiles = new ArrayList<>();
        for (Layer layer : layers) {
            collectSpawnTiles(layer, spawnTiles);
        }
        return spawnTiles;
    }

    private void collectSpawnTiles(MapComponent component, List<Tile> result) {
        if (component instanceof Tile) {
            Tile tile = (Tile) component;
            if (tile.getSpawnType() != null && !tile.getSpawnType().isEmpty()) {
                result.add(tile);
            }
        }

        for (MapComponent child : component.getChildren()) {
            collectSpawnTiles(child, result);
        }
    }

    /**
     * Gets spawn tiles filtered by spawnType (e.g., "npc", "enemy")
     */
    public List<Tile> getSpawnTilesByType(String spawnType) {
        List<Tile> result = new ArrayList<>();
        for (Layer layer : layers) {
            collectSpawnTilesByType(layer, spawnType, result);
        }
        return result;
    }

    private void collectSpawnTilesByType(MapComponent component, String spawnType, List<Tile> result) {
        if (component instanceof Tile) {
            Tile tile = (Tile) component;
            if (spawnType.equals(tile.getSpawnType())) {
                result.add(tile);
            }
        } else if (component instanceof Layer) {
            for (MapComponent child : component.getChildren()) {
                collectSpawnTilesByType(child, spawnType, result);
            }
        }
    }

    /**
     * Gets the spawn tile for a specific player ID
     * @param playerId The player ID (1 or 2)
     * @return The spawn tile for the player, or null if not found
     */
    public Tile getPlayerSpawnById(int playerId) {
        for (Layer layer : layers) {
            Tile spawn = findPlayerSpawn(layer, playerId);
            if (spawn != null) {
                return spawn;
            }
        }
        return null;
    }

    private Tile findPlayerSpawn(MapComponent component, int playerId) {
        if (component instanceof Tile) {
            Tile tile = (Tile) component;
            if ("player".equalsIgnoreCase(tile.getSpawnType()) && tile.getSpawnId() == playerId) {
                return tile;
            }
        }

        for (MapComponent child : component.getChildren()) {
            Tile found = findPlayerSpawn(child, playerId);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Gets the spawn tile for transitioning from a specific map
     * @param previousMap The previous map name (e.g., "casa/casa.tmx")
     * @return The spawn tile for the transition, or null if not found
     */
    public Tile getSpawnFromMap(String previousMap) {
        if (previousMap == null || previousMap.isEmpty()) {
            return null;
        }

        for (Layer layer : layers) {
            Tile spawn = findTransitionSpawn(layer, previousMap);
            if (spawn != null) {
                return spawn;
            }
        }
        return null;
    }

    private Tile findTransitionSpawn(MapComponent component, String previousMap) {
        if (component instanceof Tile) {
            Tile tile = (Tile) component;
            if ("transition_target".equalsIgnoreCase(tile.getSpawnType()) &&
                previousMap.equals(tile.getFromMap())) {
                return tile;
            }
        }

        for (MapComponent child : component.getChildren()) {
            Tile found = findTransitionSpawn(child, previousMap);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public String getMapName() {
        return mapName;
    }

    /**
     * Calculates the actual bounding box of non-empty tiles
     * @return Rectangle with minX, minY, width, height of actual content
     */
    public Rectangle getActualBounds() {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        boolean hasTiles = false;

        for (Layer layer : layers) {
            float[] bounds = getLayerBounds(layer);
            if (bounds != null) {
                hasTiles = true;
                minX = Math.min(minX, bounds[0]);
                minY = Math.min(minY, bounds[1]);
                maxX = Math.max(maxX, bounds[2]);
                maxY = Math.max(maxY, bounds[3]);
            }
        }

        if (!hasTiles) {
            // Fallback to theoretical dimensions if no tiles found
            return new Rectangle(0, 0, getMapWidthPixels(), getMapHeightPixels());
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Gets the bounding box of all tiles in a layer
     */
    private float[] getLayerBounds(MapComponent component) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        boolean found = false;

        if (component instanceof Tile) {
            Tile tile = (Tile) component;
            Vector2 pos = tile.getPosition();
            minX = pos.x;
            minY = pos.y;
            maxX = pos.x + tile.getWidth();
            maxY = pos.y + tile.getHeight();
            found = true;
        }

        for (MapComponent child : component.getChildren()) {
            float[] childBounds = getLayerBounds(child);
            if (childBounds != null) {
                found = true;
                minX = Math.min(minX, childBounds[0]);
                minY = Math.min(minY, childBounds[1]);
                maxX = Math.max(maxX, childBounds[2]);
                maxY = Math.max(maxY, childBounds[3]);
            }
        }

        return found ? new float[]{minX, minY, maxX, maxY} : null;
    }
}
