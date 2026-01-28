package io.SesProject.controller;






import io.SesProject.RpgGame;
import io.SesProject.controller.command.Command;
import io.SesProject.controller.command.combatCommand.UseSkillCommand;
import io.SesProject.controller.state.PlayState;
import io.SesProject.model.GameSession;
import io.SesProject.model.game.Skill;
import io.SesProject.model.game.combat.Combatant;
import io.SesProject.model.game.combat.EnemyCombatant;
import io.SesProject.model.game.combat.PlayerCombatant;
import io.SesProject.model.game.npc.NpcData;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.model.menu.MenuComposite;
import io.SesProject.model.menu.MenuItem;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.game.combat.CombatScreen;

import java.util.ArrayList;
import java.util.List;

public class CombatController extends BaseController {

    // --- GESTIONE TURNI ---
    private List<Combatant> turnQueue;
    private int turnIndex;
    private Combatant currentActor;

    // --- PARTECIPANTI ---
    private List<Combatant> heroes;
    private List<Combatant> enemies;

    // --- DATI SPECIFICI (Ponte con Esplorazione) ---
    private NpcData specificEnemyData;

    /**
     * Costruttore. Richiede obbligatoriamente i dati del nemico incontrato sulla mappa.
     */
    public CombatController(RpgGame game, AuthService authService, NpcData specificEnemyData) {
        super(game, authService);

        // Controllo di sicurezza: Non si può avviare il combattimento senza un nemico target
        if (specificEnemyData == null) {
            throw new IllegalArgumentException("[ERROR] CombatController avviato senza dati nemico!");
        }

        this.specificEnemyData = specificEnemyData;

        // Inizializzazione Liste (evita NullPointerException)
        this.heroes = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.turnQueue = new ArrayList<>();

        initializeEncounter();
    }

    private void initializeEncounter() {
        GameSession session = game.getCurrentSession();

        // 1. CARICAMENTO EROI (Co-op Locale)
        PlayerCombatant p1 = new PlayerCombatant(session.getP1());
        PlayerCombatant p2 = new PlayerCombatant(session.getP2());

        heroes.add(p1);
        heroes.add(p2);

        // 2. CARICAMENTO NEMICO SPECIFICO
        // Usiamo il costruttore Adapter di EnemyCombatant che accetta NPCData
        EnemyCombatant enemy = new EnemyCombatant(this.specificEnemyData);
        enemies.add(enemy);

        System.out.println("[COMBAT] Ingaggiato: " + enemy.getName() + " (HP: " + enemy.getMaxHp() + ")");

        // 3. COSTRUZIONE CODA TURNI (P1 -> P2 -> Nemico)
        turnQueue.add(p1);
        turnQueue.add(p2);
        turnQueue.add(enemy);

        // 4. AVVIO PRIMO TURNO
        this.turnIndex = 0;
        startTurn();
    }

    @Override
    protected BaseMenuScreen createView() {
        return new CombatScreen(this);
    }

    // --- COSTRUZIONE MENU DINAMICO ---
    public MenuComponent getDynamicCombatMenu() {
        MenuComposite root = new MenuComposite("ROOT");
        MenuComposite actions = new MenuComposite("AZIONI");

        // Crea un bottone per ogni skill posseduta dal personaggio
        for (Skill s : currentActor.getSkills()) {

            // Format del nome: "Fendente" oppure "Supernova (3)" se in cooldown
            String label = s.getName();
            if (!s.isReady()) {
                label += " (" + s.getCurrentCooldown() + ")";
            }

            // Command Pattern: Associa il comando specifico
            actions.add(new MenuItem(label, new UseSkillCommand(this, s)));
        }

        root.add(actions);
        return root;
    }


    // --- LOGICA GESTIONE TURNI ---

    private void startTurn() {
        this.currentActor = turnQueue.get(turnIndex);

        // 1. RIDUZIONE COOLDOWN (Logica di sistema)
        this.currentActor.tickCooldowns();

        // Se il personaggio è morto (HP <= 0), salta il turno
        if (currentActor.getCurrentHp() <= 0) {
            nextTurn();
            return;
        }

        System.out.println("[TURN] Inizio turno: " + currentActor.getName());

        // Se tocca al Nemico -> AI automatica
        if (currentActor instanceof EnemyCombatant) {
            simulateEnemyTurn();
        }

        if (view instanceof CombatScreen) {
            ((CombatScreen) view).updateActionMenu();
        }
    }

    public void nextTurn() {
        // Avanzamento circolare nella lista
        turnIndex = (turnIndex + 1) % turnQueue.size();
        startTurn();
    }



    public void executeSkillUsage(Skill skill) {
        if (!isPlayerTurn()) return;

        if (!skill.isReady()) {
            System.out.println("[WARNING] Abilità in ricarica! Attendi " + skill.getCurrentCooldown() + " turni.");
            return;
        }

        System.out.println("[ACTION] " + currentActor.getName() + " usa " + skill.getName());

        // Target: Per ora sempre il primo nemico (semplificazione)
        Combatant target = enemies.get(0);

        // Esecuzione effetto
        skill.use(currentActor, target);

        // Controllo vittoria
        if (!checkWinCondition()) {
            nextTurn();
        }
    }




    // --- AI NEMICA ---

    private void simulateEnemyTurn() {
        // Sceglie un bersaglio vivo a caso tra gli eroi
        Combatant target = heroes.get(0).getCurrentHp() > 0 ? heroes.get(0) : heroes.get(1);

        System.out.println("[AI] " + currentActor.getName() + " attacca " + target.getName());
        target.takeDamage(10); // Danno base nemico

        if (!checkLoseCondition()) {
            nextTurn();
        }
    }

    // --- CONDIZIONI DI VITTORIA / SCONFITTA ---

    private boolean checkWinCondition() {
        // Se il nemico è morto
        if (enemies.get(0).getCurrentHp() <= 0) {
            System.out.println("[COMBAT] VITTORIA!");

            // --- FIX LOOP MAPPA ---
            // Marchiamo l'NPC originale come sconfitto.
            // Il GameController leggerà questo flag e rimuoverà l'entità dalla mappa.
            this.specificEnemyData.setDefeated(true);

            // Torna all'esplorazione
            game.changeAppState(new PlayState());
            return true;
        }
        return false;
    }

    private boolean checkLoseCondition() {
        // Controlla se tutti gli eroi sono morti
        boolean allDead = true;
        for (Combatant h : heroes) {
            if (h.getCurrentHp() > 0) allDead = false;
        }

        if (allDead) {
            System.out.println("[COMBAT] GAME OVER...");
            // Ricarica il PlayState (o manda al menu principale/game over screen)
            game.changeAppState(new PlayState());
            return true;
        }
        return false;
    }


    // --- GETTERS PER LA VIEW ---
    public Combatant getCurrentActor() { return currentActor; }
    public List<Combatant> getHeroes() { return heroes; }
    public List<Combatant> getEnemies() { return enemies; }

    // Helper per la UI: abilita i bottoni solo se tocca a un umano
    public boolean isPlayerTurn() {
        return currentActor instanceof PlayerCombatant;
    }
}
