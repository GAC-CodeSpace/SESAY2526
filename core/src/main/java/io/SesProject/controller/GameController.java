package io.SesProject.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import io.SesProject.model.game.item.factory.SkillItemFactory;
import io.SesProject.model.game.item.factory.WeaponFactory;
import io.SesProject.model.game.map.Tile;
import io.SesProject.model.game.npc.NpcData;
import io.SesProject.model.game.npc.factory.*;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.game.GameScreen;

import java.util.ArrayList;
import java.util.List;

public class GameController extends BaseController {

    private List<GameObject> worldEntities;
    private List<InputStrategy> inputStrategies;
    private List<PlayerEntity> activePlayers;

    // Map controller for managing the game map
    private MapController mapController;

    private boolean warnedNoSolidTiles = false;

    private float prevX1, prevY1;  // Player 1 previous position
    private float prevX2, prevY2;
    private static final boolean DEBUG_COLLISIONS = false;
    // Flag per bloccare input durante le interazioni
    private boolean isDialogActive = false;
    private PlayerCharacter playerInDialog;
    // NUOVO: Riferimento all'ultima entità con cui abbiamo parlato/combattuto
    private NpcEntity lastInteractedNpc = null;

    public GameController(RpgGame game, AuthService authService) {
        super(game, authService);
        this.worldEntities = new ArrayList<>();
        this.inputStrategies = new ArrayList<>();
        this.activePlayers = new ArrayList<>();
        this.mapController = new MapController();

        initializeGame();

        game.getSystemFacade().getAudioManager().playMusic("music/exploration_music.wav");
    }

    private void initializeGame() {
        GameSession session = game.getCurrentSession();
        if (session == null) {
            System.err.println("[ERROR] Nessuna sessione attiva!");
            return;
        }
        mapController.loadLevel("Dungeon1/Dungeon_1.tmx");
        // --- 1. SETUP PLAYERS ---
        PlayerEntity p1 = new PlayerEntity(session.getP1() , game);
        worldEntities.add(p1);
        activePlayers.add(p1);
        inputStrategies.add(new KeyboardInputStrategy(p1,
            Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D));

        PlayerEntity p2 = new PlayerEntity(session.getP2() , game);
        worldEntities.add(p2);
        activePlayers.add(p2);
        inputStrategies.add(new KeyboardInputStrategy(p2,
            Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT));

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
            System.out.println("[GAME] Generazione nuovi NPC...");
            List<NpcEntity> generatedNpcs = new ArrayList<>();

            VillageNpcFactory villageFactory = new VillageNpcFactory();
            generatedNpcs.add(villageFactory.createVillager(200, 300));
            generatedNpcs.add(villageFactory.createMerchant(350, 300));
            generatedNpcs.add(villageFactory.createSolider(450 , 300));

            NpcFactory dungeonFactory = new DungeonEnemyFactory(1);
            generatedNpcs.add(dungeonFactory.createNpc(600, 500));
            generatedNpcs.add(dungeonFactory.createNpc(700, 450));

            NpcFactory bossFactory = new BossFactory(1);
            generatedNpcs.add(bossFactory.createNpc(1000, 500));

            for (NpcEntity npc : generatedNpcs) {
                worldEntities.add(npc);
                session.addNpc(npc.getData());
            }
        }
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
        // Controlliamo se ci siamo allontanati dall'ultimo NPC
        if (lastInteractedNpc != null) {
            boolean stillColliding = false;
            for (PlayerEntity player : activePlayers) {
                // Usiamo una versione leggermente più larga dell'overlap per sicurezza,
                // o il checkOverlap standard
                if (checkOverlap(player, lastInteractedNpc)) {
                    stillColliding = true;
                    break;
                }
            }

            // Se non stiamo più toccando l'NPC, "dimentichiamo" l'interazione
            // permettendo di parlarci di nuovo se ci riavviciniamo
            if (!stillColliding) {
                System.out.println("[INTERACTION] Reset per: " + lastInteractedNpc.getName());
                lastInteractedNpc = null;
            }
        }

        // 4. Update fisico entità
        for (GameObject obj : worldEntities) obj.update(delta);
        // salva posizione
        if (activePlayers.size() > 0) {
            prevX1 = activePlayers.get(0).getX();
            prevY1 = activePlayers.get(0).getY();
        }
        if (activePlayers.size() > 1) {
            prevX2 = activePlayers.get(1).getX();
            prevY2 = activePlayers.get(1).getY();
        }

        for (GameObject obj : worldEntities) obj.update(delta);
        // 5. Verifica Collisioni (una sola volta per frame è sufficiente)
        checkCollisions();

        // 6. Rimozione nemici morti
        worldEntities.removeIf(obj -> {
            if (obj instanceof NpcEntity) {
                return ((NpcEntity) obj).isDefeated();
            }
            return false;
        });

        // 7. System Input
        handleSystemInput();
    }

    private void checkCollisions() {
        for (PlayerEntity playerEntity : activePlayers) {
            // Check collisions with map tiles first
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

                        // --- CORREZIONE CRITICA ---
                        // Impostiamo il flag a TRUE *PRIMA* di chiamare interact.
                        // Così se l'NPC decide di chiudere subito (es. karma negativo),
                        // la sua chiamata a endDialogState() sarà l'ultima a essere eseguita.
                        this.isDialogActive = true;

                        // 3. Avviamo l'interazione
                        npc.interact(game, playerEntity);

                        return;
                    }
                }
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
     * Checks collisions between a player and map tiles
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
            // Player not found in list, shouldn't happen but handle gracefully
            System.err.println("[COLLISION] Warning: Player not found in activePlayers list");
            // Stop velocity as a safety measure
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

                // Collision response: revert player to previous position
                player.setPosition(prevX, prevY);
                // Also stop velocity to prevent stuttering
                player.setVelocity(0, 0);
                break;
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

            // --- NUOVA LOGICA: RICOMPENSA ITEM ---
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

        // Logica di assegnazione
        // Nota: Assumo che "Warrior" sia la stringa esatta salvata nel player
        if ("Warrior".equalsIgnoreCase(archetipo)) {
            // Genera un'arma casuale o specifica
            reward = new WeaponFactory("Sword").createItem();
        } else {
            // Per Mago o altri: Genera una Skill
            reward = new SkillItemFactory("Benedizione Antica").createItem();
        }

        if (reward != null) {
            player.addItem(reward);
            System.out.println("[REWARD] Assegnato oggetto: " + reward.getName() + " a " + player.getName());

            // Opzionale: Feedback sonoro per l'acquisizione oggetto
            game.getSystemFacade().getAudioManager().playSound("music/sfx/menu/070_Equip_10.wav", game.getSystemFacade().getAssetManager());
        }
    }

    // --- Getters ---
    public List<GameObject> getWorldEntities() {
        return worldEntities;
    }

    public RpgGame getGame(){
        return this.game;
    }

    /**
     * Gets the map controller
     */
    public MapController getMapController() {
        return mapController;
    }

    /**
     * Loads a level by filename
     */
    public void loadLevel(String filename) {
        if (mapController != null) {
            mapController.loadLevel(filename);
        }
    }
}
