package io.SesProject.controller.map;

import io.SesProject.model.game.map.GameMap;
import io.SesProject.model.game.map.Tile;
import io.SesProject.model.game.map.builder.ConcreteMapBuilder;
import io.SesProject.model.game.map.builder.MapBuilder;
import io.SesProject.model.game.map.builder.MapDirector;

import java.util.List;

/**
 * Controller for managing map loading and updates.
 * Integrates the Builder and Director patterns to load maps from TMX files.
 */
public class MapController {
    
    private GameMap currentMap;
    private MapDirector director;
    private ConcreteMapBuilder builder;
    
    public MapController() {
        this.currentMap = null;
    }
    
    /**
     * Loads a level from a TMX file
     * @param filename The name of the TMX file (e.g., "level_1.tmx")
     */
    public void loadLevel(String filename) {
        System.out.println("[MapController] Loading level: " + filename);
        
        // Construct full path to the map file
        String mapPath = "maps/" + filename;
        
        // Create builder with the TMX file path
        builder = new ConcreteMapBuilder(mapPath);
        
        // Create director and build the map
        director = new MapDirector(builder);
        director.constructLevel(filename);
        
        // Get the constructed map
        currentMap = builder.getResult();
        
        if (currentMap != null) {
            System.out.println("[MapController] Level loaded successfully: " + currentMap.getMapName());
        } else {
            System.err.println("[MapController] Failed to load level: " + filename);
        }
    }
    
    /**
     * Gets the currently loaded map
     */
    public GameMap getCurrentMap() {
        return currentMap;
    }
    
    /**
     * Gets all solid tiles from the current map for collision detection
     */
    public List<Tile> getSolidTiles() {
        if (currentMap != null) {
            return currentMap.getSolidTiles();
        }
        return null;
    }

    /**
     * Gets all transition tiles from the current map for map transitions
     */
    public List<Tile> getTransitionTiles() {
        if (currentMap != null) {
            return currentMap.getTransitionTiles();
        }
        return null;
    }
    
    /**
     * Updates the map (if needed for animated tiles, etc.)
     */
    public void update(float delta) {
        // Future: handle animated tiles or dynamic map elements
    }
    
    /**
     * Disposes of map resources
     */
    public void dispose() {
        if (builder != null) {
            builder.dispose();
        }
    }
}
