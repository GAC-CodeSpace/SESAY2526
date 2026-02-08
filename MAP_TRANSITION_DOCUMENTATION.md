# Map Transition System Documentation

## Overview

The map transition system allows automatic loading of new maps when the player steps on specially marked tiles in TMX files. This system is implemented similarly to the collision system and uses custom tile properties.

## How to Use

### 1. Setting up Transition Tiles in Tiled

To create a transition tile in your TMX file using Tiled:

1. Select a tile in your tileset
2. Open the tile properties panel
3. Add the following custom properties:

   - **`transition`** (type: boolean or int)
     - Set to `true` (or `1` for integer type)
     - Marks this tile as a transition zone

   - **`nextMap`** (type: string)
     - The filename of the next map to load
     - Example: `"Dungeon1/Dungeon_1.tmx"`
     - Must include the relative path from the `maps/` directory

Example property setup in Tiled:
```xml
<tile id="123">
  <properties>
    <property name="transition" type="bool" value="true"/>
    <property name="nextMap" value="Dungeon1/Dungeon_1.tmx"/>
  </properties>
</tile>
```

### 2. How It Works

1. When the map is loaded, `ConcreteMapBuilder` reads the `transition` and `nextMap` properties from each tile
2. Tiles with `transition=true` are tracked separately from solid tiles
3. During gameplay, when a player overlaps with a transition tile:
   - The system extracts the `nextMap` value
   - Calls `mapController.loadLevel(nextMap)` to load the new map
   - Logs the transition event

### 3. Implementation Details

#### Files Modified:

- **`Tile.java`**: Added `transition` and `nextMap` fields with getters/setters
- **`ConcreteMapBuilder.java`**: Reads transition properties from TMX and logs count
- **`GameMap.java`**: Added `getTransitionTiles()` and `collectTransitionTiles()` methods
- **`MapController.java`**: Added `getTransitionTiles()` method
- **`GameController.java`**: Added `checkMapTransitions()` method called from `checkMapCollisions()`

#### Design Patterns Used:

- **Composite Pattern**: Traverses the Layer → Tile hierarchy to find transition tiles
- **Separation of Concerns**: Map loading logic in MapController, detection in GameController
- **Consistent API**: Similar to `getSolidTiles()` → `getTransitionTiles()`

### 4. Edge Cases Handled

- ✅ Tiles without transition properties continue to work normally (backward compatible)
- ✅ Tiles with `transition=false` do not trigger transitions
- ✅ Tiles with `transition=true` but no `nextMap` are ignored
- ✅ Tiles with `transition=true` and empty `nextMap` are ignored
- ✅ Both boolean and integer (0/1) values are supported for the `transition` property

### 5. Logging

The system provides detailed logging:

```
[MapBuilder] Layer 'Terreno': 45 solid tiles found, 2 transition tiles found
[MAP TRANSITION] Player 'Player1' triggered transition at (320.0, 480.0) - Loading map: Dungeon1/Dungeon_1.tmx
[MapController] Loading level: Dungeon1/Dungeon_1.tmx
```

## Testing

Comprehensive tests are available in `TransitionSystemTest.java`:

- ✅ Tile creation with transition properties
- ✅ Tile creation without transition properties (backward compatibility)
- ✅ Tile setters and getters
- ✅ GameMap collection of transition tiles
- ✅ Multiple layers with transition tiles
- ✅ Edge cases (null nextMap, empty nextMap, no transition tiles)

All 9 transition system tests pass, along with all 20 existing tests in the map system.

## Future Enhancements

- Player position reset after transition (currently not implemented)
- Transition animations or fade effects
- Transition zones (multiple tiles forming a transition area)
- Conditional transitions based on game state
