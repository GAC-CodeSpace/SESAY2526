package io.SesProject.model.game.map.builder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import io.SesProject.model.game.map.GameMap;
import io.SesProject.model.game.map.Layer;
import io.SesProject.model.game.map.Tile;

/**
 * Concrete implementation of MapBuilder that reads TMX files
 * and constructs the map structure.
 */
public class ConcreteMapBuilder implements MapBuilder {
    
    private GameMap gameMap;
    private TiledMap tiledMap;
    private String tmxFilePath;
    
    public ConcreteMapBuilder(String tmxFilePath) {
        this.tmxFilePath = tmxFilePath;
    }
    
    @Override
    public void reset(String mapName) {
        this.gameMap = new GameMap(mapName);
        
        // Load TMX file using LibGDX's TmxMapLoader
        try {
            TmxMapLoader loader = new TmxMapLoader();
            this.tiledMap = loader.load(tmxFilePath);
            System.out.println("[MapBuilder] Loaded TMX: " + tmxFilePath);
        } catch (Exception e) {
            System.err.println("[MapBuilder] Error loading TMX file: " + e.getMessage());
            this.tiledMap = null;
        }
    }
    
    @Override
    public void buildTerrainLayer() {
        if (tiledMap == null) return;
        
        MapLayer mapLayer = tiledMap.getLayers().get("terrain");
        if (mapLayer != null && mapLayer instanceof TiledMapTileLayer) {
            Layer terrainLayer = new Layer("terrain", 0);
            populateLayerFromTiledLayer(terrainLayer, (TiledMapTileLayer) mapLayer);
            gameMap.addLayer(terrainLayer);
            System.out.println("[MapBuilder] Built terrain layer");
        }
    }
    
    @Override
    public void buildPlayerLevel() {
        if (tiledMap == null) return;
        
        MapLayer mapLayer = tiledMap.getLayers().get("player");
        if (mapLayer != null && mapLayer instanceof TiledMapTileLayer) {
            Layer playerLayer = new Layer("player", 1);
            populateLayerFromTiledLayer(playerLayer, (TiledMapTileLayer) mapLayer);
            gameMap.addLayer(playerLayer);
            System.out.println("[MapBuilder] Built player layer");
        }
    }
    
    @Override
    public void buildDecorationLevel() {
        if (tiledMap == null) return;
        
        MapLayer mapLayer = tiledMap.getLayers().get("decoration");
        if (mapLayer != null && mapLayer instanceof TiledMapTileLayer) {
            Layer decorationLayer = new Layer("decoration", 2);
            populateLayerFromTiledLayer(decorationLayer, (TiledMapTileLayer) mapLayer);
            gameMap.addLayer(decorationLayer);
            System.out.println("[MapBuilder] Built decoration layer");
        }
    }
    
    @Override
    public void buildEntityLayer() {
        if (tiledMap == null) return;
        
        MapLayer mapLayer = tiledMap.getLayers().get("entity");
        if (mapLayer != null && mapLayer instanceof TiledMapTileLayer) {
            Layer entityLayer = new Layer("entity", 3);
            populateLayerFromTiledLayer(entityLayer, (TiledMapTileLayer) mapLayer);
            gameMap.addLayer(entityLayer);
            System.out.println("[MapBuilder] Built entity layer");
        }
    }
    
    /**
     * Populates a Layer with Tiles from a TiledMapTileLayer
     */
    private void populateLayerFromTiledLayer(Layer layer, TiledMapTileLayer tiledLayer) {
        int width = tiledLayer.getWidth();
        int height = tiledLayer.getHeight();
        int tileWidth = (int) tiledLayer.getTileWidth();
        int tileHeight = (int) tiledLayer.getTileHeight();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    // Get texture from tile
                    TextureRegion textureRegion = cell.getTile().getTextureRegion();
                    
                    // Check if tile is solid (check tile properties)
                    boolean isSolid = false;
                    MapProperties props = cell.getTile().getProperties();
                    if (props.containsKey("solid")) {
                        isSolid = props.get("solid", Boolean.class);
                    }
                    
                    // Create Tile with world coordinates
                    float worldX = x * tileWidth;
                    float worldY = y * tileHeight;
                    Tile tile = new Tile(worldX, worldY, tileWidth, tileHeight, textureRegion, isSolid);
                    layer.addChild(tile);
                }
            }
        }
    }
    
    @Override
    public GameMap getResult() {
        return gameMap;
    }
    
    /**
     * Disposes of the TiledMap resources
     */
    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
    }
}
