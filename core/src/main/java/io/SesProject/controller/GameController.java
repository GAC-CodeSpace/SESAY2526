package io.SesProject.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.SesProject.RpgGame;
import io.SesProject.controller.game.controllerInputStrategy.InputStrategy;
import io.SesProject.controller.game.controllerInputStrategy.KeyboardInputStrategy;
import io.SesProject.controller.map.MapController;
import io.SesProject.controller.state.CombatState;
import io.SesProject.controller.state.PausedState;
import io.SesProject.model.GameSession;
import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.GameObject;
import io.SesProject.model.game.PlayerEntity;
import io.SesProject.model.game.item.factory.Item;
import io.SesProject.model.game.item.factory.PowerUpItem;
import io.SesProject.model.game.item.factory.SkillItemFactory;
import io.SesProject.model.game.item.factory.WeaponFactory;
import io.SesProject.model.game.map.GameMap;
import io.SesProject.model.game.map.Tile;
import io.SesProject.model.game.npc.NpcData;
import io.SesProject.model.game.npc.factory.*;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.game.GameScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController extends BaseController {

    private List<GameObject> worldEntities;
    private List<InputStrategy> inputStrategies;
    private List<PlayerEntity> activePlayers;

    // Map controller for managing the game map
    private MapController mapController;

    // --- INTEGRATO DA GameController1: Gestione posizioni precedenti per collisioni ---
    private float prevX1, prevY1;  // Player 1 previous position
    private float prevX2, prevY2;  // Player 2 previous position
    private Map<NpcEntity, Vector2> npcPreviousPositions;

    // Debug flags
    private boolean warnedNoSolidTiles = false;
    private static final boolean DEBUG_COLLISIONS = false;
    // --------------------------------------------------------------------------------

    // Flag per bloccare input durante le interazioni
    private boolean isDialogActive = false;
    private PlayerCharacter playerInDialog;

    // Riferimento all'ultima entità con cui abbiamo parlato/combattuto
    private NpcEntity lastInteractedNpc = null;

    public GameController(RpgGame game, AuthService authService) {
        super(game, authService);
        this.worldEntities = new ArrayList<>();
        this.inputStrategies = new ArrayList<>();
        this.activePlayers = new ArrayList<>();
        this.mapController = new MapController();
        this.npcPreviousPositions = new HashMap<>();
        initializeGame();

        // INTEGRATO: Usiamo la musica definita in GameController1 (o puoi mantenere exploration_music)
        game.getSystemFacade().getAudioManager().playMusic("music/exploration_music.wav");
    }

    private void initializeGame() {
        GameSession session = game.getCurrentSession();
        if (session == null) {
            System.err.println("[ERROR] Nessuna sessione attiva!");
            return;
        }

        // INTEGRATO: Caricamento esplicito del livello
        mapController.loadLevel("casa/casa.tmx");

        // --- 1. SETUP PLAYERS ---
        // Nota: Mantengo il costruttore (session, game) del tuo GameController originale
        PlayerEntity p1 = new PlayerEntity(session.getP1(), game);
        worldEntities.add(p1);
        activePlayers.add(p1);
        inputStrategies.add(new KeyboardInputStrategy(p1,
            Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D));
        p1.setPosition(500, 300);
        PlayerEntity p2 = new PlayerEntity(session.getP2(), game);
        worldEntities.add(p2);
        activePlayers.add(p2);
        inputStrategies.add(new KeyboardInputStrategy(p2,
            Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT));
        p2.setPosition(550, 320);

         positionPlayersAtSpawn(p1,p2);
        // --- 2. SETUP NPC ---
        if (!session.getWorldNpcs().isEmpty()) {
            System.out.println("[GAME] Caricamento NPC da salvataggio...");
            for (NpcData data : session.getWorldNpcs()) {
                if (data.isDefeated()) continue;
                if (data.isHostile()) {
                    worldEntities.add(new HostileNpc(data));
                } else {
                    worldEntities.add(new FriendlyNpc(data));
                }
            }
        } else {
            System.out.println("[GAME] Generazione NPC dalla mappa...");
            spawnNpcsFromMap();
        }
    }

    private void spawnNpcsFromMap() {
        GameSession session = game.getCurrentSession();
        GameMap currentMap = mapController.getCurrentMap();

        if (currentMap == null) {
            System.err.println("[GAME] No map loaded for NPC spawning");
            return;
        }

        // 1. Spawn friendly NPCs
        List<Tile> npcSpawns = mapController.getSpawnTilesByType("npc");
        System.out.println("[GAME] Found " + npcSpawns.size() + " NPC spawn points in map");

        VillageNpcFactory villageFactory = new VillageNpcFactory();

        for (Tile spawn : npcSpawns) {
            String npcName = spawn.getNpcName();

            // Skip if npcName is null or empty
            if (npcName == null || npcName.isEmpty()) {
                System.err.println("[GAME] NPC spawn at (" + spawn.getPosition().x + ", " + spawn.getPosition().y + ") has no npcName");
                continue;
            }

            float x = spawn.getPosition().x;
            float y = spawn.getPosition().y;

            NpcEntity npc = null;

            switch (npcName.toLowerCase()) {
                case "villico":
                    npc = villageFactory.createVillager(x, y);
                    break;
                case "mercante":
                    npc = villageFactory.createMerchant(x, y);
                    break;
                case "soldato":
                    npc = villageFactory.createSolider(x, y);
                    break;
                default:
                    System.err.println("[GAME] Unknown NPC type: " + npcName);
                    continue;
            }

            if (npc != null) {
                worldEntities.add(npc);
                session.addNpc(npc.getData());
                System.out.println("[GAME] Spawned NPC: " + npcName + " at (" + x + ", " + y + ")");
            }
        }

        // 2. Spawn enemies
        List<Tile> enemySpawns = mapController.getSpawnTilesByType("enemy");
        System.out.println("[GAME] Found " + enemySpawns.size() + " enemy spawn points in map");

        for (Tile spawn : enemySpawns) {
            int level = spawn.getSpawnId(); // Use spawnId as enemy level
            float x = spawn.getPosition().x;
            float y = spawn.getPosition().y;
            String enemyType = spawn.getNpcName(); // "boss" or empty for regular enemies

            NpcFactory enemyFactory;

            if ("boss".equalsIgnoreCase(enemyType)) {
                enemyFactory = new BossFactory(level);
            } else {
                enemyFactory = new DungeonEnemyFactory(level);
            }

            NpcEntity enemy = enemyFactory.createNpc(x, y);
            worldEntities.add(enemy);
            session.addNpc(enemy.getData());
            System.out.println("[GAME] Spawned enemy level " + level + " at (" + x + ", " + y + ")");
        }
    }

    public void onMapChanged() {
        System.out.println("[GAME] Map changed - Reloading NPCs...");

        GameSession session = game.getCurrentSession();

        // Remove all current NPCs from world entities
        worldEntities.removeIf(obj -> obj instanceof NpcEntity);

        // CRITICAL FIX: Clear NPCs from the session to prevent them from being reloaded on wrong maps
        if (session != null) {
            session.getWorldNpcs().clear();
            System.out.println("[GAME] Cleared NPC data from session");
        }

        // Respawn NPCs from the new map only (based on spawn tiles)
        spawnNpcsFromMap();
    }
    @Override
    protected BaseMenuScreen createView() {
        return new GameScreen(this);
    }

    /**
     * Game Loop Principale.
     */
    public void update(float delta) {
        // 1. Se c'è un dialogo, blocca tutto (tranne animazioni se vuoi)
        if (isDialogActive) {
            for(PlayerEntity p : activePlayers) {
                p.setVelocity(0, 0);
            }
            // Non fare altro se siamo in dialogo
            return;
        }

        // 2. Input e Movimento (Solo se NON c'è dialogo)
        for (InputStrategy s : inputStrategies) s.handleInput();

        // 3. Logica di Reset "Cooldown" Interazione
        if (lastInteractedNpc != null) {
            boolean stillColliding = false;
            for (PlayerEntity player : activePlayers) {
                if (checkOverlap(player, lastInteractedNpc)) {
                    stillColliding = true;
                    break;
                }
            }

            if (!stillColliding) {
                System.out.println("[INTERACTION] Reset per: " + lastInteractedNpc.getName());
                lastInteractedNpc = null;
            }
        }

        // --- INTEGRATO DA GameController1: 4. Save previous positions before update ---
        if (activePlayers.size() > 0) {
            prevX1 = activePlayers.get(0).getX();
            prevY1 = activePlayers.get(0).getY();
        }
        if (activePlayers.size() > 1) {
            prevX2 = activePlayers.get(1).getX();
            prevY2 = activePlayers.get(1).getY();
        }
        for (GameObject obj : worldEntities) {
            if (obj instanceof NpcEntity) {
                NpcEntity npc = (NpcEntity) obj;
                npcPreviousPositions.put(npc, new Vector2(npc.getX(), npc.getY()));
            }
        }


        // 5. Update fisico entità
        for (GameObject obj : worldEntities) obj.update(delta);

        // 6. Verifica Collisioni (una sola volta per frame è sufficiente)
        checkCollisions();

        // 7. Rimozione nemici morti
        worldEntities.removeIf(obj -> {
            if (obj instanceof NpcEntity) {
                return ((NpcEntity) obj).isDefeated();
            }
            return false;
        });

        // 8. System Input
        handleSystemInput();
    }

    private void checkCollisions() {
        for (PlayerEntity playerEntity : activePlayers) {
            // Check collisions with map tiles first (Using logic from GameController1)
            checkMapCollisions(playerEntity);

            for (GameObject obj : worldEntities) {
                if (obj == playerEntity || obj instanceof PlayerEntity) continue;

                if (checkOverlap(playerEntity, obj)) {
                    if (obj instanceof NpcEntity) {
                        NpcEntity npc = (NpcEntity) obj;

                        // 1. Controllo Anti-Loop (Cooldown)
                        if (npc == lastInteractedNpc) {
                            continue;
                        }

                        System.out.println("[GAME] Interazione avviata da: " + playerEntity.getName());

                        // 2. Salviamo il riferimento
                        this.lastInteractedNpc = npc;

                        // 3. Blocchiamo dialog state
                        this.isDialogActive = true;

                        // 4. Avviamo l'interazione
                        npc.interact(game, playerEntity);

                        return;
                    }
                }
            }
        }
        for (GameObject obj : worldEntities) {
            if (obj instanceof NpcEntity) {
                checkNpcMapCollisions((NpcEntity) obj);
            }
        }
    }

    private boolean checkOverlap(GameObject a, GameObject b) {
        return a.getX() < b.getX() + b.getWidth() &&
            a.getX() + a.getWidth() > b.getX() &&
            a.getY() < b.getY() + b.getHeight() &&
            a.getY() + a.getHeight() > b.getY();
    }

    /**
     * Checks collisions between a player and map tiles.
     * INTEGRATO DA GameController1: Usa prevX/prevY per evitare di rimanere incastrati.
     */
    private void checkMapCollisions(PlayerEntity player) {
        if (mapController == null || mapController.getSolidTiles() == null) {
            return;
        }

        List<Tile> solidTiles = mapController.getSolidTiles();

        // Debug: Log number of solid tiles (only once)
        if (solidTiles.isEmpty() && !warnedNoSolidTiles) {
            System.out.println("[COLLISION] Warning: No solid tiles found in map");
            warnedNoSolidTiles = true;
        }

        // Determine which previous position to use
        float prevX, prevY;
        int playerIndex = activePlayers.indexOf(player);
        if (playerIndex == 0) {
            prevX = prevX1;
            prevY = prevY1;
        } else if (playerIndex == 1) {
            prevX = prevX2;
            prevY = prevY2;
        } else {
            // Player not found or extra player
            player.setVelocity(0, 0);
            return;
        }

        for (Tile tile : solidTiles) {
            if (checkOverlapWithTile(player, tile)) {
                // Debug logging (only if enabled)
                if (DEBUG_COLLISIONS) {
                    System.out.println(String.format(
                        "[COLLISION] Player '%s' collided with tile at (%.1f, %.1f) - Reverting to (%.1f, %.1f)",
                        player.getName(), tile.getPosition().x, tile.getPosition().y, prevX, prevY
                    ));
                }

                // INTEGRATO: Collision response -> revert player to previous position
                player.setPosition(prevX, prevY);
                // Also stop velocity to prevent stuttering
                player.setVelocity(0, 0);
                break;
            }
        }

        // Check for map transitions
        checkMapTransitions(player);
    }

    /**
     * 🎯 AGGIUNTO: Checks collisions between an NPC and map tiles
     */
    private void checkNpcMapCollisions(NpcEntity npc) {
        if (mapController == null || mapController.getSolidTiles() == null) {
            return;
        }

        List<Tile> solidTiles = mapController.getSolidTiles();

        // Get NPC previous position
        Vector2 prevPos = npcPreviousPositions.get(npc);
        if (prevPos == null) {
            // First frame, no previous position
            return;
        }

        float prevX = prevPos.x;
        float prevY = prevPos.y;

        for (Tile tile : solidTiles) {
            if (checkOverlapWithTile(npc, tile)) {
                // Debug logging (only if enabled)
                if (DEBUG_COLLISIONS) {
                    System.out.println(String.format(
                        "[COLLISION] NPC '%s' collided with tile at (%.1f, %.1f) - Reverting to (%.1f, %.1f)",
                        npc.getName(), tile.getPosition().x, tile.getPosition().y, prevX, prevY
                    ));
                }

                // Collision response -> revert NPC to previous position
                npc.setPosition(prevX, prevY);
                // Also stop velocity to prevent stuttering
                npc.setVelocity(0, 0);

                // Update NpcData position
                npc.getData().setPosition(prevX, prevY);

                break;
            }
        }
    }

    /**
     * Checks if a player is stepping on a transition tile and triggers map loading
     */
    private void checkMapTransitions(PlayerEntity player) {
        if (mapController == null || mapController.getTransitionTiles() == null) {
            return;
        }

        List<Tile> transitionTiles = mapController.getTransitionTiles();

        for (Tile tile : transitionTiles) {
            if (checkOverlapWithTile(player, tile)) {
                String nextMap = tile.getNextMap();
                if (nextMap != null && !nextMap.isEmpty()) {
                    System.out.println(String.format(
                        "[MAP TRANSITION] Player '%s' triggered transition at (%.1f, %.1f) - Loading map: %s",
                        player.getName(), tile.getPosition().x, tile.getPosition().y, nextMap
                    ));
                    // Carica la nuova mappa
                    mapController.loadLevel(nextMap);

                    // Reload NPCs for the new map
                    onMapChanged();

                    // Riposiziona tutti i player dopo la transizione
                    if (activePlayers.size() >= 2) {
                        positionPlayersAtSpawn(activePlayers.get(0), activePlayers.get(1));
                    }

// 🎯 AGGIUNTO: Aggiorna la camera nella GameScreen per la nuova mappa
                    if (game.getScreen() instanceof GameScreen) {
                        ((GameScreen) game.getScreen()).updateCameraForCurrentMap();
                    }
                    return;
                }
            }
        }
    }

    /**
     * Checks if a game object overlaps with a tile
     */
    private boolean checkOverlapWithTile(GameObject obj, Tile tile) {
        float tileX = tile.getPosition().x;
        float tileY = tile.getPosition().y;
        return obj.getX() < tileX + tile.getWidth() &&
            obj.getX() + obj.getWidth() > tileX &&
            obj.getY() < tileY + tile.getHeight() &&
            obj.getY() + obj.getHeight() > tileY;
    }

    public void setPlayerInDialog(PlayerCharacter pc) {
        this.playerInDialog = pc;
    }

    private void handleSystemInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Se c'è un dialogo aperto, ESC non fa nulla (o potrebbe chiudere il dialogo)
            if (!isDialogActive) {
                System.out.println("[GAME] Pausa richiesta.");
                game.changeAppState(new PausedState());
            }
        }
    }

    // --- Metodi per la gestione del Dialogo (Chiamati dalla View) ---

    public void startDialogState() {
        this.isDialogActive = true;
    }

    public void endDialogState() {
        this.isDialogActive = false;
    }

    public void handleDialogChoice(boolean isGoodChoice) {
        // Controllo di sicurezza
        if (playerInDialog == null) {
            endDialogState();
            return;
        }

        if (isGoodChoice) {
            System.out.println("[KARMA] Scelta buona. " + playerInDialog.getName() + " guadagna Karma.");
            playerInDialog.modifyKarma(5);

            // Ricompensa
            giveRewardBasedOnArchetype(playerInDialog);

        } else {
            System.out.println("[KARMA] Scelta cattiva. " + playerInDialog.getName() + " perde Karma.");
            playerInDialog.modifyKarma(-15);
        }

        // Reset e chiusura
        this.playerInDialog = null;
        endDialogState();
    }

    public void startCombatFromDialog(NpcData enemyData) {
        System.out.println("[GAME] Transizione verso CombatState contro: " + enemyData.getName());
        endDialogState();
        game.changeAppState(new CombatState(enemyData));
    }

    /**
     * Metodo privato per generare e assegnare la ricompensa
     */
    private void giveRewardBasedOnArchetype(PlayerCharacter player) {
        Item reward = null;
        String archetipo = player.getArchetype();

        if ("Warrior".equalsIgnoreCase(archetipo)) {
            reward = new WeaponFactory("Sword").createItem();
        } else {
            reward = new SkillItemFactory("Benedizione Antica").createItem();
        }

        if (reward != null) {
            player.addItem(reward);
            System.out.println("[REWARD] Assegnato oggetto: " + reward.getName() + " a " + player.getName());

            // Suono reward
            game.getSystemFacade().getAudioManager().playSound("music/sfx/menu/070_Equip_10.wav", game.getSystemFacade().getAssetManager());
        }

        if (Math.random() < 0.20) {
            player.addItem(new PowerUpItem());
            System.out.println("[REWARD] Fortunato! Ricevuta anche una Sfera del Potere per " + player.getName());

            // Notifica a schermo (se hai accesso alla view qui, altrimenti usa i log)
            if (game.getScreen() instanceof GameScreen) {
                ((GameScreen) game.getScreen()).showMessage("Dono Raro", "L'NPC ti ha donato anche una Sfera del Potere!");
            }
        }}
    // --- Getters ---
    public List<GameObject> getWorldEntities() {
        return worldEntities;
    }

    public RpgGame getGame(){
        return this.game;
    }

    public MapController getMapController() {
        return mapController;
    }

    public void loadLevel(String filename) {
        if (mapController != null) {
            mapController.loadLevel(filename);
        }
    }

    private void positionPlayersAtSpawn(PlayerEntity p1, PlayerEntity p2) {
        if (mapController == null || mapController.getCurrentMap() == null) {
            System.out.println("[SPAWN] No map loaded, using default positions");
            return;
        }

        GameMap currentMap = mapController.getCurrentMap();
        String previousMap = mapController.getPreviousMapName();

        // Try to spawn based on transition first
        if (previousMap != null && !previousMap.isEmpty()) {
            System.out.println("[SPAWN] Looking for transition spawn from: " + previousMap);
            Tile transitionSpawn = currentMap.getSpawnFromMap(previousMap);

            if (transitionSpawn != null) {
                float spawnX = transitionSpawn.getPosition().x;
                float spawnY = transitionSpawn.getPosition().y;
                System.out.println(String.format("[SPAWN] Found transition spawn at (%.1f, %.1f)", spawnX, spawnY));

                // Position both players at the same transition spawn point
                p1.setPosition(spawnX, spawnY);
                p2.setPosition(spawnX, spawnY);

                System.out.println(String.format("[SPAWN] Player 1 positioned at (%.1f, %.1f)",
                    p1.getX(), p1.getY()));
                System.out.println(String.format("[SPAWN] Player 2 positioned at (%.1f, %.1f)",
                    p2.getX(), p2.getY()));
                return;
            } else {
                System.out.println("[SPAWN] No transition spawn found for map: " + previousMap);
            }
        }

        // Try to spawn at player spawn points
        System.out.println("[SPAWN] Looking for player spawn points");
        Tile player1Spawn = currentMap.getPlayerSpawnById(1);
        Tile player2Spawn = currentMap.getPlayerSpawnById(2);

        if (player1Spawn != null) {
            float spawnX = player1Spawn.getPosition().x;
            float spawnY = player1Spawn.getPosition().y;
            p1.setPosition(spawnX, spawnY);
            System.out.println(String.format("[SPAWN] Player 1 spawned at player spawn (%.1f, %.1f)",
                spawnX, spawnY));
        } else {
            System.out.println("[SPAWN] No spawn point found for Player 1, using default position");
        }

        if (player2Spawn != null) {
            float spawnX = player2Spawn.getPosition().x;
            float spawnY = player2Spawn.getPosition().y;
            p2.setPosition(spawnX, spawnY);
            System.out.println(String.format("[SPAWN] Player 2 spawned at player spawn (%.1f, %.1f)",
                spawnX, spawnY));
        } else {
            System.out.println("[SPAWN] No spawn point found for Player 2, using default position");
        }
    }
}
