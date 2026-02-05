package io.SesProject.model.game.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents a layer in the map that can contain multiple components.
 * Composite node in the Composite Pattern.
 */
public class Layer extends MapComponent {
    
    private String name;
    private int zOrder;
    
    public Layer(String name, int zOrder) {
        super();
        this.name = name;
        this.zOrder = zOrder;
    }
    
    @Override
    public void render(SpriteBatch batch) {
        // Render all children in this layer
        for (MapComponent child : children) {
            child.render(batch);
        }
    }
    
    @Override
    public boolean isSolid() {
        // A layer itself is not solid, but its children might be
        return false;
    }
    
    public String getName() {
        return name;
    }
    
    public int getZOrder() {
        return zOrder;
    }
    
    public void setZOrder(int zOrder) {
        this.zOrder = zOrder;
    }
}
