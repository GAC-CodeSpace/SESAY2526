package io.sesProject.map;

import com.badlogic.gdx.math.Rectangle;
import io.SesProject.model.game.map.GameMap;
import io.SesProject.model.game.map.Layer;
import io.SesProject.model.game.map.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the bounding box calculation functionality.
 */
public class BoundingBoxTest {

    private GameMap gameMap;
    private Layer terrainLayer;

    @BeforeEach
    public void setUp() {
        gameMap = new GameMap("test_map");
        gameMap.setDimensions(46, 43, 32, 32);
        terrainLayer = new Layer("terrain", 0);
    }

    @Test
    public void testEmptyMapBounds() {
        gameMap.addLayer(terrainLayer);
        Rectangle bounds = gameMap.getActualBounds();

        // Empty map should return theoretical dimensions
        assertNotNull(bounds);
        assertEquals(0, bounds.x);
        assertEquals(0, bounds.y);
        assertEquals(46 * 32, bounds.width);
        assertEquals(43 * 32, bounds.height);
    }

    @Test
    public void testSingleTileBounds() {
        Tile tile = new Tile(100, 200, 32, 32, null, true);
        terrainLayer.addChild(tile);
        gameMap.addLayer(terrainLayer);

        Rectangle bounds = gameMap.getActualBounds();

        assertNotNull(bounds);
        assertEquals(100, bounds.x, 0.001);
        assertEquals(200, bounds.y, 0.001);
        assertEquals(32, bounds.width, 0.001);
        assertEquals(32, bounds.height, 0.001);
    }

    @Test
    public void testMultipleTilesBounds() {
        Tile tile1 = new Tile(100, 100, 32, 32, null, true);
        Tile tile2 = new Tile(200, 200, 32, 32, null, true);
        Tile tile3 = new Tile(150, 150, 32, 32, null, false);

        terrainLayer.addChild(tile1);
        terrainLayer.addChild(tile2);
        terrainLayer.addChild(tile3);
        gameMap.addLayer(terrainLayer);

        Rectangle bounds = gameMap.getActualBounds();

        assertNotNull(bounds);
        // Min should be at tile1's position
        assertEquals(100, bounds.x, 0.001);
        assertEquals(100, bounds.y, 0.001);
        // Max should extend to tile2's far edge
        assertEquals(200 + 32 - 100, bounds.width, 0.001);
        assertEquals(200 + 32 - 100, bounds.height, 0.001);
    }

    @Test
    public void testTilesWithOffsetBounds() {
        // Simulate a map with empty tiles at the borders
        Tile tile1 = new Tile(320, 320, 32, 32, null, true); // Far from origin
        Tile tile2 = new Tile(352, 352, 32, 32, null, true);

        terrainLayer.addChild(tile1);
        terrainLayer.addChild(tile2);
        gameMap.addLayer(terrainLayer);

        Rectangle bounds = gameMap.getActualBounds();

        assertNotNull(bounds);
        assertEquals(320, bounds.x, 0.001);
        assertEquals(320, bounds.y, 0.001);
        assertEquals(352 + 32 - 320, bounds.width, 0.001);
        assertEquals(352 + 32 - 320, bounds.height, 0.001);
    }

    @Test
    public void testMultipleLayersBounds() {
        Layer layer1 = new Layer("layer1", 0);
        Layer layer2 = new Layer("layer2", 1);

        Tile tile1 = new Tile(50, 50, 32, 32, null, true);
        Tile tile2 = new Tile(250, 250, 32, 32, null, true);

        layer1.addChild(tile1);
        layer2.addChild(tile2);

        gameMap.addLayer(layer1);
        gameMap.addLayer(layer2);

        Rectangle bounds = gameMap.getActualBounds();

        // Should encompass both layers
        assertNotNull(bounds);
        assertEquals(50, bounds.x, 0.001);
        assertEquals(50, bounds.y, 0.001);
        assertEquals(250 + 32 - 50, bounds.width, 0.001);
        assertEquals(250 + 32 - 50, bounds.height, 0.001);
    }

    @Test
    public void testNegativeCoordinatesBounds() {
        // Edge case: tiles with negative coordinates
        Tile tile1 = new Tile(-100, -100, 32, 32, null, true);
        Tile tile2 = new Tile(100, 100, 32, 32, null, true);

        terrainLayer.addChild(tile1);
        terrainLayer.addChild(tile2);
        gameMap.addLayer(terrainLayer);

        Rectangle bounds = gameMap.getActualBounds();

        assertNotNull(bounds);
        assertEquals(-100, bounds.x, 0.001);
        assertEquals(-100, bounds.y, 0.001);
        assertEquals(100 + 32 - (-100), bounds.width, 0.001);
        assertEquals(100 + 32 - (-100), bounds.height, 0.001);
    }
}
