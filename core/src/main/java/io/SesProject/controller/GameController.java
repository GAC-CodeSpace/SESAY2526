package io.SesProject.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.SesProject.RpgGame;
import io.SesProject.controller.game.controllerInputStrategy.InputStrategy;
import io.SesProject.controller.game.controllerInputStrategy.KeyboardInputStrategy;
import io.SesProject.controller.state.CombatState;
import io.SesProject.controller.state.PausedState;
import io.SesProject.model.GameSession;
import io.SesProject.model.game.GameObject;
import io.SesProject.model.game.PlayerEntity;
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

    // Flag per bloccare input durante le interazioni
    private boolean isDialogActive = false;
    // NUOVO: Riferimento all'ultima entità con cui abbiamo parlato/combattuto
    private NpcEntity lastInteractedNpc = null;

    public GameController(RpgGame game, AuthService authService) {
        super(game, authService);
        this.worldEntities = new ArrayList<>();
        this.inputStrategies = new ArrayList<>();
        this.activePlayers = new ArrayList<>();

        initializeGame();

        game.getSystemFacade().getAudioManager().playMusic("music/AdhesiveWombat-Night Shade.mp3");
    }

    private void initializeGame() {
        GameSession session = game.getCurrentSession();
        if (session == null) {
            System.err.println("[ERROR] Nessuna sessione attiva!");
            return;
        }

        // --- 1. SETUP PLAYERS ---
        PlayerEntity p1 = new PlayerEntity(session.getP1());
        worldEntities.add(p1);
        activePlayers.add(p1);
        inputStrategies.add(new KeyboardInputStrategy(p1,
            Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D));

        PlayerEntity p2 = new PlayerEntity(session.getP2());
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

        // --- CORREZIONE: BLOCCO INPUT ---
        // Se il dialogo è attivo, fermiamo i giocatori e non processiamo input/collisioni.
        if (isDialogActive) {
            for(PlayerEntity p : activePlayers) {
                p.setVelocity(0, 0); // Ferma l'inerzia
            }
        } else {
            // Se non c'è dialogo, il gioco procede normalmente
            // 1. Input Strategies
            for (InputStrategy s : inputStrategies) s.handleInput();

            // 3. Collisioni
            checkCollisions();
        }

        // --- LOGICA DI RESET INTERAZIONE ---
        // Se non sto più collidendo con l'NPC con cui ho appena finito di parlare,
        // posso parlargli di nuovo se mi riavvicino.
        if (lastInteractedNpc != null) {
            // Controlliamo se almeno UN giocatore è ancora sopra l'NPC
            boolean stillColliding = false;
            for (PlayerEntity player : activePlayers) {
                if (checkOverlap(player, lastInteractedNpc)) {
                    stillColliding = true;
                    break;
                }
            }
            // Se nessuno tocca più l'NPC, resetta lo stato
            if (!stillColliding) {
                System.out.println("[INTERACTION] Reset per: " + lastInteractedNpc.getName());
                lastInteractedNpc = null;
            }
        }

        if (!isDialogActive) {
            checkCollisions();
        }

        // 3. Collisioni
        if (!isDialogActive) {
            checkCollisions();
        }

        // 2. Logic Updates (Animazioni e movimento NPC continuano anche durante il dialogo)
        for (GameObject obj : worldEntities) obj.update(delta);

        // Rimozione nemici morti
        worldEntities.removeIf(obj -> {
            if (obj instanceof NpcEntity) {
                return ((NpcEntity) obj).isDefeated();
            }
            return false;
        });

        // 4. System Input
        handleSystemInput();
    }

    private void checkCollisions() {
        for (PlayerEntity player : activePlayers) {
            for (GameObject obj : worldEntities) {
                if (obj == player || obj instanceof PlayerEntity) continue;

                // Controlliamo solo se è un NPCEntity
                if (obj instanceof NpcEntity) {
                    NpcEntity npc = (NpcEntity) obj;

                    // --- NUOVO CONTROLLO: Posso interagire con questo NPC? ---
                    // Se sto collidendo E non è lo stesso NPC con cui ho appena finito
                    if (checkOverlap(player, npc) && npc != lastInteractedNpc) {

                        // Memorizza questo NPC come "appena usato"
                        lastInteractedNpc = npc;

                        System.out.println("[GAME] Interazione con: " + npc.getName());
                        npc.interact(game);
                        return; // Esce per evitare interazioni multiple
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
        if (isGoodChoice) {
            System.out.println("[KARMA] Scelta buona effettuata. (+Karma)");
        } else {
            System.out.println("[KARMA] Scelta cattiva effettuata. (-Karma)");
        }
        endDialogState();
    }

    public void startCombatFromDialog(NpcData enemyData) {
        System.out.println("[GAME] Transizione verso CombatState contro: " + enemyData.getName());
        endDialogState();
        game.changeAppState(new CombatState(enemyData));
    }

    // --- Getters ---
    public List<GameObject> getWorldEntities() {
        return worldEntities;
    }

    public RpgGame getGame(){
        return this.game;
    }
}
