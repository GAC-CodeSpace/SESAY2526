# Map System Documentation

## Overview
The map system implements a comprehensive tile-based map loading and rendering system using the Composite and Builder design patterns. Maps are loaded from TMX files (Tiled Map Editor format) and rendered with proper z-ordering.

## Architecture

### Design Patterns
1. **Composite Pattern**: Used for hierarchical map structure
   - `MapComponent`: Abstract base class
   - `Layer`: Composite node containing tiles/entities
   - `Tile`: Leaf node representing individual tiles

2. **Builder Pattern**: Used for constructing maps from TMX data
   - `MapBuilder`: Interface defining build steps
   - `ConcreteMapBuilder`: Implements TMX parsing and map construction
   - `MapDirector`: Orchestrates the building process

### Core Components

#### MapComponent (Abstract)
- Base class for all map elements
- Methods: `getPosition()`, `getChildren()`, `render()`, `isSolid()`

#### Tile
- Represents individual map tiles
- Properties: position, size, texture, solid flag
- Used for collision detection

#### Layer
- Container for organizing map components
- Properties: name, z-order
- Four standard layers: terrain, player, decoration, entity

#### GameMap
- Main data structure holding all layers
- Methods:
  - `addLayer(Layer)`: Adds and sorts layers by z-order
  - `getLayer(String)`: Retrieves layer by name
  - `getLayers()`: Returns all layers
  - `render(SpriteBatch)`: Renders all layers in order
  - `getSolidTiles()`: Returns tiles for collision detection

#### MapBuilder
Interface with methods:
- `reset(String)`: Initialize new map
- `buildTerrainLayer()`: Build background layer (z-order: 0)
- `buildPlayerLevel()`: Build ground level (z-order: 1)
- `buildDecorationLevel()`: Build foreground (z-order: 2)
- `buildEntityLayer()`: Build entity layer (z-order: 3)
- `getResult()`: Return constructed map

#### MapController
- Manages map loading and lifecycle
- Methods:
  - `loadLevel(String)`: Load map from TMX file
  - `getCurrentMap()`: Get active map
  - `getSolidTiles()`: Get collidable tiles
  - `update(float)`: Update map state

## Usage

### Loading a Map
```java
// In GameController initialization
mapController = new MapController();
mapController.loadLevel("test_level.tmx");
```

### Rendering
```java
// In GameScreen.render()
GameMap map = controller.getMapController().getCurrentMap();
if (map != null) {
    map.render(spriteBatch);
}
```

### Collision Detection
```java
// Check collisions with map tiles
List<Tile> solidTiles = mapController.getSolidTiles();
for (Tile tile : solidTiles) {
    if (checkOverlapWithTile(player, tile)) {
        // Handle collision
    }
}
```

## Creating TMX Maps

### Requirements
- Use Tiled Map Editor (https://www.mapeditor.org/)
- Tile dimensions: 32x32 pixels
- Layer names must match: "terrain", "player", "decoration", "entity"

### Tile Properties
Add custom properties to tiles for collision:
- `solid` (boolean): Set to true for collidable tiles

### Example Structure
```
Map (20x15 tiles)
├── Layer: terrain (z-order: 0)
├── Layer: player (z-order: 1)
├── Layer: decoration (z-order: 2)
└── Layer: entity (z-order: 3)
```

### File Location
Place TMX files in: `assets/maps/`

## Rendering Order (Z-Depth)
0. Terrain Layer (background)
1. Player Level Layer (ground)
2. Decoration Layer (foreground)
3. Entity Layer + Game Entities
4. UI Elements (highest)

## Integration Points

### GameController
- Field: `MapController mapController`
- Method: `loadLevel(String)` - Load a map
- Collision detection updated to check map tiles

### GameObject
- Field: `boolean solid` - Collision flag
- Methods: `isSolid()`, `setSolid(boolean)`

### GameScreen
- Renders map before entities
- Ensures proper z-ordering

## Testing
Unit tests are available in `MapSystemTest.java`:
- Test map creation and layer management
- Test tile properties and collision
- Test layer ordering by z-index

Run tests:
```bash
./gradlew test --tests "io.sesProject.map.MapSystemTest"
```

## Future Enhancements
- Animated tiles support
- Dynamic tile updates
- Object layers for spawn points
- Multiple tileset support
- Map transitions
- Parallax scrolling backgrounds
