package io.sesProject.map;

import io.SesProject.controller.map.MapController;
import io.SesProject.model.game.map.GameMap;
import io.SesProject.model.game.map.Layer;
import io.SesProject.model.game.map.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the map transition system.
 */
public class TransitionSystemTest {

    private MapController mapController;
    private GameMap gameMap;

    @BeforeEach
    public void setUp() {
        mapController = new MapController();
        gameMap = new GameMap("test_map");
    }

    @Test
    public void testTileWithTransitionProperties() {
        // Create a tile with transition properties
        Tile transitionTile = new Tile(0, 0, 16, 16, null, false, true, "level_2.tmx");

        assertTrue(transitionTile.isTransition());
        assertEquals("level_2.tmx", transitionTile.getNextMap());
        assertFalse(transitionTile.isSolid());
    }

    @Test
    public void testTileWithoutTransitionProperties() {
        // Create a tile using the old constructor
        Tile regularTile = new Tile(0, 0, 16, 16, null, true);

        assertFalse(regularTile.isTransition());
        assertNull(regularTile.getNextMap());
        assertTrue(regularTile.isSolid());
    }

    @Test
    public void testTileSettersAndGetters() {
        Tile tile = new Tile(0, 0, 16, 16, null, false);

        tile.setTransition(true);
        tile.setNextMap("dungeon.tmx");

        assertTrue(tile.isTransition());
        assertEquals("dungeon.tmx", tile.getNextMap());
    }

    @Test
    public void testGameMapGetTransitionTiles() {
        // Create a layer with multiple tiles
        Layer layer = new Layer("test_layer", 0);

        // Add a transition tile
        Tile transitionTile1 = new Tile(0, 0, 16, 16, null, false, true, "level_2.tmx");
        layer.addChild(transitionTile1);

        // Add a non-transition tile
        Tile regularTile = new Tile(16, 0, 16, 16, null, true, false, null);
        layer.addChild(regularTile);

        // Add another transition tile
        Tile transitionTile2 = new Tile(32, 0, 16, 16, null, false, true, "level_3.tmx");
        layer.addChild(transitionTile2);

        gameMap.addLayer(layer);

        // Get transition tiles
        List<Tile> transitionTiles = gameMap.getTransitionTiles();

        assertNotNull(transitionTiles);
        assertEquals(2, transitionTiles.size());
        assertTrue(transitionTiles.contains(transitionTile1));
        assertTrue(transitionTiles.contains(transitionTile2));
        assertFalse(transitionTiles.contains(regularTile));
    }

    @Test
    public void testGameMapNoTransitionTiles() {
        // Create a layer with only regular tiles
        Layer layer = new Layer("test_layer", 0);

        Tile regularTile1 = new Tile(0, 0, 16, 16, null, true, false, null);
        Tile regularTile2 = new Tile(16, 0, 16, 16, null, false, false, null);

        layer.addChild(regularTile1);
        layer.addChild(regularTile2);

        gameMap.addLayer(layer);

        // Get transition tiles
        List<Tile> transitionTiles = gameMap.getTransitionTiles();

        assertNotNull(transitionTiles);
        assertEquals(0, transitionTiles.size());
    }

    @Test
    public void testMapControllerGetTransitionTilesWhenNoMapLoaded() {
        // Should not throw exception when no map is loaded
        List<Tile> transitionTiles = mapController.getTransitionTiles();
        assertNull(transitionTiles);
    }

    @Test
    public void testTransitionTileWithNullNextMap() {
        // Transition tile with null nextMap
        Tile tile = new Tile(0, 0, 16, 16, null, false, true, null);

        assertTrue(tile.isTransition());
        assertNull(tile.getNextMap());
    }

    @Test
    public void testTransitionTileWithEmptyNextMap() {
        // Transition tile with empty nextMap
        Tile tile = new Tile(0, 0, 16, 16, null, false, true, "");

        assertTrue(tile.isTransition());
        assertEquals("", tile.getNextMap());
    }

    @Test
    public void testMultipleLayersWithTransitionTiles() {
        // Create multiple layers with transition tiles
        Layer layer1 = new Layer("layer1", 0);
        Layer layer2 = new Layer("layer2", 1);

        Tile transitionTile1 = new Tile(0, 0, 16, 16, null, false, true, "map1.tmx");
        Tile transitionTile2 = new Tile(16, 0, 16, 16, null, false, true, "map2.tmx");
        Tile regularTile = new Tile(32, 0, 16, 16, null, true, false, null);

        layer1.addChild(transitionTile1);
        layer1.addChild(regularTile);
        layer2.addChild(transitionTile2);

        gameMap.addLayer(layer1);
        gameMap.addLayer(layer2);

        // Get transition tiles
        List<Tile> transitionTiles = gameMap.getTransitionTiles();

        assertNotNull(transitionTiles);
        assertEquals(2, transitionTiles.size());
        assertTrue(transitionTiles.contains(transitionTile1));
        assertTrue(transitionTiles.contains(transitionTile2));
    }
}
