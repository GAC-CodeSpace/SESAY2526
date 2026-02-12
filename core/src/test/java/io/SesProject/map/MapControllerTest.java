package io.sesProject.map;

import io.SesProject.controller.map.MapController;
import io.SesProject.model.game.map.GameMap;
import io.SesProject.model.game.map.Layer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for MapController.
 * Note: These tests verify the controller logic without requiring LibGDX graphics context.
 */
public class MapControllerTest {

    private MapController mapController;

    @BeforeEach
    public void setUp() {
        mapController = new MapController();
    }

    @Test
    public void testMapControllerCreation() {
        assertNotNull(mapController);
        assertNull(mapController.getCurrentMap());
    }

    @Test
    public void testGetSolidTilesWhenNoMapLoaded() {
        // Should not throw exception when no map is loaded
        var solidTiles = mapController.getSolidTiles();
        assertNull(solidTiles);
    }

    @Test
    public void testUpdateMethodDoesNotThrowException() {
        // Update should work even with no map loaded
        assertDoesNotThrow(() -> mapController.update(0.016f));
    }

    @Test
    public void testDisposeMethodDoesNotThrowException() {
        // Dispose should work safely
        assertDoesNotThrow(() -> mapController.dispose());
    }
}
