package io.SesProject.examples;

import io.SesProject.controller.map.MapController;
import io.SesProject.model.game.map.GameMap;
import io.SesProject.model.game.map.Tile;

import java.util.List;

/**
 * Example demonstrating how to use the Map System.
 * This class shows the basic workflow for loading and using maps.
 */
public class MapSystemExample {

    public static void main(String[] args) {
        System.out.println("=== Map System Usage Example ===\n");

        // Step 1: Create a MapController
        MapController mapController = new MapController();
        System.out.println("1. MapController created");

        // Step 2: Load a level
        // Note: This requires LibGDX runtime, so we're just demonstrating the API
        System.out.println("2. To load a map, call: mapController.loadLevel(\"test_level.tmx\")");

        // Step 3: Get the current map
        System.out.println("3. Access the map: GameMap map = mapController.getCurrentMap()");

        // Step 4: Render the map (in your game loop)
        System.out.println("4. In render method: map.render(spriteBatch)");

        // Step 5: Get solid tiles for collision detection
        System.out.println("5. For collision: List<Tile> solidTiles = mapController.getSolidTiles()");

        System.out.println("\n=== Map Structure ===");
        System.out.println("Maps contain layers in this order (z-depth):");
        System.out.println("  0. Terrain Layer (background)");
        System.out.println("  1. Player Level Layer (ground)");
        System.out.println("  2. Decoration Layer (foreground)");
        System.out.println("  3. Entity Layer (spawn points)");

        System.out.println("\n=== TMX Map Requirements ===");
        System.out.println("- Place TMX files in: assets/maps/");
        System.out.println("- Layer names: 'terrain', 'player', 'decoration', 'entity'");
        System.out.println("- Tile property 'solid' (boolean) for collision detection");
        System.out.println("- Standard tile size: 32x32 pixels");

        System.out.println("\n=== Integration Example ===");
        System.out.println("// In GameController initialization:");
        System.out.println("mapController = new MapController();");
        System.out.println("mapController.loadLevel(\"level_1.tmx\");");
        System.out.println("");
        System.out.println("// In GameScreen render:");
        System.out.println("GameMap map = controller.getMapController().getCurrentMap();");
        System.out.println("if (map != null) {");
        System.out.println("    map.render((SpriteBatch) stage.getBatch());");
        System.out.println("}");
        System.out.println("");
        System.out.println("// For collision detection:");
        System.out.println("List<Tile> solidTiles = mapController.getSolidTiles();");
        System.out.println("for (Tile tile : solidTiles) {");
        System.out.println("    if (checkOverlapWithTile(player, tile)) {");
        System.out.println("        // Handle collision");
        System.out.println("    }");
        System.out.println("}");

        System.out.println("\n=== Example Complete ===");
    }
}
