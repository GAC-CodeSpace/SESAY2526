package io.SesProject.controller;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.SesProject.RpgGame;
import io.SesProject.controller.game.controllerInputStrategy.InputStrategy;
import io.SesProject.controller.game.controllerInputStrategy.KeyboardInputStrategy;
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

    // Lista polimorfica: contiene Players, Villici, Mostri, Boss.
    // La View itera su questa lista per disegnare tutto senza sapere cosa sia.
    private List<GameObject> worldEntities;

    // Lista delle strategie di input (solo per entità controllabili dall'uomo)
    private List<InputStrategy> inputStrategies;

    // Riferimento ai player per logica collisioni rapida
    private List<PlayerEntity> activePlayers;

    public GameController(RpgGame game, AuthService authService) {
        super(game, authService);
        this.worldEntities = new ArrayList<>();
        this.inputStrategies = new ArrayList<>();
        this.activePlayers = new ArrayList<>();

        initializeGame();

        // Avvio musica esplorazione
        game.getSystemFacade().getAudioManager().playMusic("music/AdhesiveWombat-Night Shade.mp3");
    }

    /**
     * Inizializzazione del mondo di gioco.
     * Qui si vede l'integrazione tra Memento (Sessione) e Factory Method (NPC).
     */
    private void initializeGame() {
        GameSession session = game.getCurrentSession();
        if (session == null) {
            System.err.println("[ERROR] Nessuna sessione attiva!");
            return;
        }

        // --- 1. SETUP PLAYERS (Co-op Locale) ---

        // Player 1
        PlayerEntity p1 = new PlayerEntity(session.getP1());
        worldEntities.add(p1);
        activePlayers.add(p1);
        // Strategy Input: WASD
        inputStrategies.add(new KeyboardInputStrategy(p1,
            Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D));

        // Player 2
        PlayerEntity p2 = new PlayerEntity(session.getP2());
        worldEntities.add(p2);
        activePlayers.add(p2);
        // Strategy Input: FRECCE
        inputStrategies.add(new KeyboardInputStrategy(p2,
            Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT));

        // --- DEFINIZIONE DEL LIVELLO CORRENTE ---
        // (In futuro questo valore verrà letto dal salvataggio: session.getCurrentLevel())
        if (!session.getWorldNpcs().isEmpty()) {
            System.out.println("[GAME] Caricamento NPC da salvataggio (" + session.getWorldNpcs().size() + " trovati).");

            for (NpcData data : session.getWorldNpcs()) {
                // Se è stato sconfitto, NON lo ricreiamo nel mondo
                if (data.isDefeated()) {
                    continue;
                }

                // Ricreiamo l'Entità di Gioco dai Dati
                // Dobbiamo distinguere tra Friendly e Hostile per usare la classe giusta
                if (data.isHostile()) {
                    worldEntities.add(new HostileNpc(data));
                } else {
                    worldEntities.add(new FriendlyNpc(data));
                }
            }
        }

        // CASO B: NUOVA PARTITA (Nessun NPC in memoria) -> Usiamo le Factory
        else {
            System.out.println("[GAME] Generazione nuovi NPC da Factory.");

            List<NpcEntity> generatedNpcs = new ArrayList<>();

            // Factory Villaggio
            NpcFactory villageFactory = new VillageNpcFactory();
            generatedNpcs.add(villageFactory.createNpc(200, 300));
            generatedNpcs.add(villageFactory.createNpc(350, 300));

            // Factory Dungeon
            NpcFactory dungeonFactory = new DungeonEnemyFactory(1);
            generatedNpcs.add(dungeonFactory.createNpc(600, 500));
            generatedNpcs.add(dungeonFactory.createNpc(700, 450));

            // Factory Boss
            NpcFactory bossFactory = new BossFactory(1);
            generatedNpcs.add(bossFactory.createNpc(1000, 500));

            // AGGIUNTA AL MONDO E REGISTRAZIONE NELLA SESSIONE
            for (NpcEntity npc : generatedNpcs) {
                // Aggiunge al mondo di gioco (per update/render)
                worldEntities.add(npc);

                // IMPORTANTE: Registra i dati nella sessione per i futuri salvataggi!
                // Per farlo in modo pulito, dobbiamo esporre i dati da NPCEntity
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
        // 1. Input Strategies
        for (InputStrategy s : inputStrategies) s.handleInput();

        // 2. Logic Updates
        for (GameObject obj : worldEntities) obj.update(delta);

        // --- CORREZIONE: RIMOZIONE NEMICI MORTI ---
        // Rimuove dalla lista le entità che sono NPCEntity E hanno i dati "defeated"
        // Usiamo removeIf (Java 8+) per evitare ConcurrentModificationException
        worldEntities.removeIf(obj -> {
            if (obj instanceof NpcEntity) {
                return ((NpcEntity) obj).isDefeated(); // Serve aggiungere isDefeated() in NPCEntity
            }
            return false;
        });

        // 3. Collisioni
        checkCollisions();

        // 4. System Input
        handleSystemInput();
    }

    /**
     * Controlla se un giocatore tocca un NPC.
     */
    private void checkCollisions() {
        for (PlayerEntity player : activePlayers) {
            for (GameObject obj : worldEntities) {
                // Non controlliamo collisione con se stessi o altri player per ora
                if (obj == player || obj instanceof PlayerEntity) continue;

                // Check collisione semplice (AABB - Axis Aligned Bounding Box)
                if (checkOverlap(player, obj)) {

                    // Se tocchiamo un NPC, attiviamo la sua interazione
                    if (obj instanceof NpcEntity) {
                        System.out.println("[GAME] Interazione con: " + ((NpcEntity) obj).getName());

                        // Chiama il metodo polimorfico (Dialogo o Combattimento)
                        // Passiamo 'game' perché HostileNPC deve cambiare stato
                        ((NpcEntity) obj).interact(game);

                        // Nota: Se cambia stato (es. CombatState), questo update() si ferma qui
                        // perché il RPGGame smette di chiamare render() su questa schermata.
                        return;
                    }
                }
            }
        }
    }

    /**
     * Helper per collisione rettangolare semplice.
     */
    private boolean checkOverlap(GameObject a, GameObject b) {
        return a.getX() < b.getX() + b.getWidth() &&
            a.getX() + a.getWidth() > b.getX() &&
            a.getY() < b.getY() + b.getHeight() &&
            a.getY() + a.getHeight() > b.getY();
    }

    private void handleSystemInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("[GAME] Pausa richiesta.");
            game.changeAppState(new PausedState());
        }

    }

    // Getter per la View
    public List<GameObject> getWorldEntities() {
        return worldEntities;
    }
}
