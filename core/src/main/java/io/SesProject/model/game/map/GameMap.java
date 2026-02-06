package io.SesProject.model.game.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

/**
 * Main data structure for holding map information.
 * Manages multiple layers in rendering order.
 */
public class GameMap {
    
    private List<Layer> layers;
    private String mapName;
    
    public GameMap(String mapName) {
        this.mapName = mapName;
        this.layers = new ArrayList<>();
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
    
    public String getMapName() {
        return mapName;
    }
}
