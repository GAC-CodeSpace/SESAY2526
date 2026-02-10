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
    private boolean transition;
    private String nextMap;

    // Spawn properties
    private String spawnType;      // "player", "transition_target", "npc", "enemy", etc.
    private int spawnId;           // 1, 2 (for player 1 and 2)
    private String fromMap;        // "casa/casa.tmx", etc.
    private String npcName;        // "Villico", "Mercante", "Soldato", "boss", etc.

    public Tile(float x, float y, int width, int height, TextureRegion texture, boolean solid) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.solid = solid;
        this.transition = false;
        this.nextMap = null;
        this.spawnType = "";
        this.spawnId = 0;
        this.fromMap = "";
        this.npcName = "";
    }

    public Tile(float x, float y, int width, int height, TextureRegion texture, boolean solid, boolean transition, String nextMap) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.solid = solid;
        this.transition = transition;
        this.nextMap = nextMap;
        this.spawnType = "";
        this.spawnId = 0;
        this.fromMap = "";
        this.npcName = "";
    }

    public Tile(float x, float y, int width, int height, TextureRegion texture, boolean solid,
                boolean transition, String nextMap, String spawnType, int spawnId, String fromMap) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.solid = solid;
        this.transition = transition;
        this.nextMap = nextMap;
        this.spawnType = spawnType != null ? spawnType : "";
        this.spawnId = spawnId;
        this.fromMap = fromMap != null ? fromMap : "";
        this.npcName = "";
    }

    public Tile(float x, float y, int width, int height, TextureRegion texture, boolean solid,
                boolean transition, String nextMap, String spawnType, int spawnId, String fromMap, String npcName) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.solid = solid;
        this.transition = transition;
        this.nextMap = nextMap;
        this.spawnType = spawnType != null ? spawnType : "";
        this.spawnId = spawnId;
        this.fromMap = fromMap != null ? fromMap : "";
        this.npcName = npcName != null ? npcName : "";
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

    public boolean isTransition() {
        return transition;
    }

    public void setTransition(boolean transition) {
        this.transition = transition;
    }

    public String getNextMap() {
        return nextMap;
    }

    public void setNextMap(String nextMap) {
        this.nextMap = nextMap;
    }

    public String getSpawnType() {
        return spawnType;
    }

    public void setSpawnType(String spawnType) {
        this.spawnType = spawnType;
    }

    public int getSpawnId() {
        return spawnId;
    }

    public void setSpawnId(int spawnId) {
        this.spawnId = spawnId;
    }

    public String getFromMap() {
        return fromMap;
    }

    public void setFromMap(String fromMap) {
        this.fromMap = fromMap;
    }

    public String getNpcName() {
        return npcName;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName != null ? npcName : "";
    }
}
