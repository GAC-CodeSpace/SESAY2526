package io.SesProject.map;

import io.SesProject.model.game.map.GameMap;
import io.SesProject.model.game.map.Layer;
import io.SesProject.model.game.map.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the map system components.
 */
public class MapSystemTest {

    private GameMap gameMap;
    private Layer terrainLayer;
    private Layer playerLayer;

    @BeforeEach
    public void setUp() {
        gameMap = new GameMap("test_map");
        terrainLayer = new Layer("terrain", 0);
        playerLayer = new Layer("player", 1);
    }

    @Test
    public void testGameMapCreation() {
        assertNotNull(gameMap);
        assertEquals("test_map", gameMap.getMapName());
        assertNotNull(gameMap.getLayers());
        assertTrue(gameMap.getLayers().isEmpty());
    }

    @Test
    public void testAddLayer() {
        gameMap.addLayer(terrainLayer);
        assertEquals(1, gameMap.getLayers().size());
        assertEquals("terrain", gameMap.getLayers().get(0).getName());
    }

    @Test
    public void testLayerOrdering() {
        gameMap.addLayer(playerLayer);  // z-order 1
        gameMap.addLayer(terrainLayer); // z-order 0
        
        // Layers should be sorted by z-order
        assertEquals("terrain", gameMap.getLayers().get(0).getName());
        assertEquals("player", gameMap.getLayers().get(1).getName());
    }

    @Test
    public void testGetLayerByName() {
        gameMap.addLayer(terrainLayer);
        gameMap.addLayer(playerLayer);
        
        Layer found = gameMap.getLayer("terrain");
        assertNotNull(found);
        assertEquals("terrain", found.getName());
        
        Layer notFound = gameMap.getLayer("nonexistent");
        assertNull(notFound);
    }

    @Test
    public void testTileCreation() {
        Tile tile = new Tile(0, 0, 32, 32, null, true);
        assertNotNull(tile);
        assertTrue(tile.isSolid());
        assertEquals(0, tile.getPosition().x);
        assertEquals(0, tile.getPosition().y);
        assertEquals(32, tile.getWidth());
        assertEquals(32, tile.getHeight());
    }

    @Test
    public void testLayerChildren() {
        Tile tile1 = new Tile(0, 0, 32, 32, null, true);
        Tile tile2 = new Tile(32, 0, 32, 32, null, false);
        
        terrainLayer.addChild(tile1);
        terrainLayer.addChild(tile2);
        
        assertEquals(2, terrainLayer.getChildren().size());
    }

    @Test
    public void testGetSolidTiles() {
        Tile solidTile = new Tile(0, 0, 32, 32, null, true);
        Tile nonSolidTile = new Tile(32, 0, 32, 32, null, false);
        
        terrainLayer.addChild(solidTile);
        terrainLayer.addChild(nonSolidTile);
        gameMap.addLayer(terrainLayer);
        
        var solidTiles = gameMap.getSolidTiles();
        assertEquals(1, solidTiles.size());
        assertTrue(solidTiles.get(0).isSolid());
    }

    @Test
    public void testTileSetSolid() {
        Tile tile = new Tile(0, 0, 32, 32, null, false);
        assertFalse(tile.isSolid());
        
        tile.setSolid(true);
        assertTrue(tile.isSolid());
    }

    @Test
    public void testLayerZOrder() {
        assertEquals(0, terrainLayer.getZOrder());
        assertEquals(1, playerLayer.getZOrder());
        
        terrainLayer.setZOrder(5);
        assertEquals(5, terrainLayer.getZOrder());
    }
}
