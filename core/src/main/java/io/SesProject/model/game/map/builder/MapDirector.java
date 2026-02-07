package io.SesProject.model.game.map.builder;

/**
 * Director class that orchestrates the map building process.
 * Uses a MapBuilder to construct a complete map structure step by step.
 */
public class MapDirector {

    private MapBuilder builder;

    public MapDirector(MapBuilder builder) {
        this.builder = builder;
    }

    public void setBuilder(MapBuilder builder) {
        this.builder = builder;
    }

    /**
     * Constructs a complete level by calling all builder methods in sequence
     */
    public void constructLevel(String mapName) {
        System.out.println("[MapDirector] Constructing level: " + mapName);

        // Reset builder for a new map
        builder.reset(mapName);

        // Build all layers in order
        builder.buildTerrainLayer();
        builder.buildPlayerLevel();
        builder.buildDecorationLevel();
        builder.buildEntityLayer();


        System.out.println("[MapDirector] Level construction complete");
    }
}
