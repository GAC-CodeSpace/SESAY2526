# Map System Architecture

## Class Hierarchy

```
MapComponent (abstract)
├── Tile (leaf)
│   ├── position: Vector2
│   ├── texture: TextureRegion
│   ├── solid: boolean
│   └── width, height: int
│
└── Layer (composite)
    ├── name: String
    ├── zOrder: int
    └── children: List<MapComponent>

GameMap
├── layers: List<Layer>
├── mapName: String
└── methods:
    ├── addLayer(Layer)
    ├── getLayer(String)
    ├── getLayers()
    ├── render(SpriteBatch)
    └── getSolidTiles()
```

## Builder Pattern Structure

```
<<interface>> MapBuilder
├── reset(String)
├── buildTerrainLayer()
├── buildPlayerLevel()
├── buildDecorationLevel()
├── buildEntityLayer()
└── getResult(): GameMap

ConcreteMapBuilder implements MapBuilder
├── gameMap: GameMap
├── tiledMap: TiledMap
├── tmxFilePath: String
└── populateLayerFromTiledLayer()

MapDirector
├── builder: MapBuilder
└── constructLevel(String)
```

## Controller Structure

```
MapController
├── currentMap: GameMap
├── director: MapDirector
├── builder: ConcreteMapBuilder
└── methods:
    ├── loadLevel(String)
    ├── getCurrentMap(): GameMap
    ├── getSolidTiles(): List<Tile>
    ├── update(float)
    └── dispose()
```

## Integration Flow

```
GameController
    │
    ├─── MapController mapController
    │        │
    │        └─── loadLevel("test_level.tmx")
    │                 │
    │                 ├─── MapDirector
    │                 │       │
    │                 │       └─── ConcreteMapBuilder
    │                 │               │
    │                 │               └─── TmxMapLoader (LibGDX)
    │                 │
    │                 └─── GameMap (result)
    │
    └─── checkMapCollisions(player)
             │
             └─── getSolidTiles()

GameScreen
    │
    └─── render(delta)
             │
             ├─── map.render(spriteBatch)  [First]
             └─── entities.render()         [Second]
```

## Data Flow

1. **Loading Phase**:
   ```
   User → GameController.loadLevel()
        → MapController.loadLevel()
        → MapDirector.constructLevel()
        → ConcreteMapBuilder (builds layers from TMX)
        → GameMap (complete map structure)
   ```

2. **Rendering Phase**:
   ```
   GameScreen.render()
        → MapController.getCurrentMap()
        → GameMap.render()
        → Layer.render() (for each layer, in z-order)
        → Tile.render() (for each tile in layer)
   ```

3. **Collision Phase**:
   ```
   GameController.checkCollisions()
        → checkMapCollisions()
        → MapController.getSolidTiles()
        → GameMap.getSolidTiles()
        → Check overlap with each solid tile
   ```

## Layer Z-Order (Rendering)

```
┌─────────────────────────────────────┐
│  UI Elements (z=4)                  │  ← Highest
├─────────────────────────────────────┤
│  Entity Layer + Sprites (z=3)       │
├─────────────────────────────────────┤
│  Decoration Layer (z=2)             │
├─────────────────────────────────────┤
│  Player Level Layer (z=1)           │
├─────────────────────────────────────┤
│  Terrain Layer (z=0)                │  ← Lowest
└─────────────────────────────────────┘
```

## Design Pattern Benefits

### Composite Pattern
- **Flexibility**: Treat individual tiles and groups of tiles uniformly
- **Extensibility**: Easy to add new component types
- **Hierarchy**: Natural tree structure for map organization

### Builder Pattern
- **Complexity Management**: Step-by-step construction of complex map
- **Separation**: TMX parsing logic separated from map structure
- **Reusability**: Same builder can construct different maps
- **Flexibility**: Easy to add new layer types or parsing strategies

## File Organization

```
io.SesProject
├── controller
│   ├── GameController.java (modified)
│   └── map
│       └── MapController.java (new)
│
├── model.game
│   ├── GameObject.java (modified)
│   └── map
│       ├── MapComponent.java (new)
│       ├── Tile.java (new)
│       ├── Layer.java (new)
│       ├── GameMap.java (new)
│       └── builder
│           ├── MapBuilder.java (new)
│           ├── ConcreteMapBuilder.java (new)
│           └── MapDirector.java (new)
│
└── view.game
    └── GameScreen.java (modified)

assets
└── maps
    └── test_level.tmx (new)
```

## Dependencies

- **LibGDX Core**: Provides TiledMap, TmxMapLoader, SpriteBatch
- **No Additional Dependencies**: Uses only built-in LibGDX features

## Testing Strategy

1. **Unit Tests**: Test individual components (Tile, Layer, GameMap)
2. **Integration Tests**: Test MapController without graphics context
3. **Manual Tests**: Visual verification with actual TMX maps

## Future Extensions

1. **Animated Tiles**: Add animation support to Tile class
2. **Object Layers**: Parse object layers for spawn points
3. **Multiple Tilesets**: Support maps with multiple tilesets
4. **Parallax Layers**: Add depth effect to background layers
5. **Dynamic Maps**: Support runtime tile updates
6. **Map Transitions**: Handle transitions between different maps
