package io.SesProject.model.game.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for the Composite Pattern in the map system.
 * Represents any element that can be part of the map hierarchy.
 */
public abstract class MapComponent {
    
    protected Vector2 position;
    protected List<MapComponent> children;
    
    public MapComponent() {
        this.position = new Vector2(0, 0);
        this.children = new ArrayList<>();
    }
    
    public MapComponent(float x, float y) {
        this.position = new Vector2(x, y);
        this.children = new ArrayList<>();
    }
    
    /**
     * Gets the position of this component
     */
    public Vector2 getPosition() {
        return position;
    }
    
    /**
     * Sets the position of this component
     */
    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }
    
    /**
     * Gets the children of this component
     */
    public List<MapComponent> getChildren() {
        return children;
    }
    
    /**
     * Adds a child component
     */
    public void addChild(MapComponent child) {
        children.add(child);
    }
    
    /**
     * Removes a child component
     */
    public void removeChild(MapComponent child) {
        children.remove(child);
    }
    
    /**
     * Renders this component and all its children
     */
    public abstract void render(SpriteBatch batch);
    
    /**
     * Returns whether this component is solid (for collision detection)
     */
    public abstract boolean isSolid();
}
