package io.SesProject.model.game.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents an individual tile in the map.
 * Leaf node in the Composite Pattern.
 */
public class Tile extends MapComponent {

    private TextureRegion texture;
    private boolean solid;
    private int width;
    private int height;

    public Tile(float x, float y, int width, int height, TextureRegion texture, boolean solid) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.solid = solid;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }

    @Override
    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }
}
