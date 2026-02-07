package io.SesProject.model.game.map.builder;

import io.SesProject.model.game.map.GameMap;

/**
 * Builder interface for constructing maps from TMX data.
 * Defines methods for building each layer type.
 */
public interface MapBuilder {

    /**
     * Resets the builder to start building a new map
     */
    void reset(String mapName);

    /**
     * Builds the terrain layer (background tiles)
     */
    void buildTerrainLayer();

    /**
     * Builds the player level layer (ground level)
     */
    void buildPlayerLevel();

    /**
     * Builds the decoration layer (foreground decorations)
     */
    void buildDecorationLevel();

    /**
     * Builds the entity layer (spawn points, objects)
     */
    void buildEntityLayer();

    /**
     * Returns the constructed map
     */
    GameMap getResult();
}
