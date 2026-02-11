package io.SesProject.controller;






import io.SesProject.RpgGame;
import io.SesProject.controller.command.Command;
import io.SesProject.controller.command.combatCommand.UseSkillCommand;
import io.SesProject.controller.state.GameOverState;
import io.SesProject.controller.state.PlayState;
import io.SesProject.controller.state.VictoryState;
import io.SesProject.model.GameSession;
import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.Skill;
import io.SesProject.model.game.combat.CombatReward;
import io.SesProject.model.game.combat.Combatant;
import io.SesProject.model.game.combat.EnemyCombatant;
import io.SesProject.model.game.combat.PlayerCombatant;
import io.SesProject.model.game.npc.NpcData;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.model.menu.MenuComposite;
import io.SesProject.model.menu.MenuItem;
import io.SesProject.service.AuthService;
import io.SesProject.service.SystemFacade;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.game.combat.CombatScreen;
import io.SesProject.view.game.combat.StatusEffect;

import java.util.*;
import java.util.stream.Collectors;


public class CombatController extends BaseController {

    // --- GESTIONE TURNI ---
    private List<Combatant> turnQueue;
    private int turnIndex;
    private Combatant currentActor;

    // --- PARTECIPANTI ---
    private List<Combatant> heroes;
    private List<Combatant> enemies;

    // --- DATI SPECIFICI ---
    private NpcData specificEnemyData;
    private CombatReward combatReward;

    // Utilità per il random
    private Random random;

    public CombatController(RpgGame game, AuthService authService, NpcData specificEnemyData) {
        super(game, authService);

        if (specificEnemyData == null) {
            throw new IllegalArgumentException("[ERROR] CombatController avviato senza dati nemico!");
        }

        this.specificEnemyData = specificEnemyData;
        this.heroes = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.turnQueue = new ArrayList<>();
        this.random = new Random();

        initializeEncounter();
    }

    private List<Combatant> getFullContext() {
        List<Combatant> all = new ArrayList<>();
        all.addAll(heroes);
        all.addAll(enemies);
        return all;
    }

    private void initializeEncounter() {
        GameSession session = game.getCurrentSession();

        // 1. CARICAMENTO EROI
        PlayerCombatant p1 = new PlayerCombatant(session.getP1());
        PlayerCombatant p2 = new PlayerCombatant(session.getP2());

        heroes.add(p1);
        heroes.add(p2);

        // 2. CARICAMENTO NEMICO SPECIFICO
        EnemyCombatant enemy = new EnemyCombatant(this.specificEnemyData);
        enemies.add(enemy);

        System.out.println("[COMBAT] Ingaggiato: " + enemy.getName() +
            " (HP: " + enemy.getMaxHp() + ", ATK: " + enemy.getAttackPower() + ")");

        // 3. COSTRUZIONE CODA TURNI INIZIALE
        turnQueue.add(p1);
        turnQueue.add(p2);
        turnQueue.add(enemy);

        // --- NUOVO: ORDINAMENTO TURNI (Warrior First) ---
        // Ordiniamo la lista in modo che il Warrior sia sempre all'indice 0.
        Collections.sort(turnQueue, new Comparator<Combatant>() {
            @Override
            public int compare(Combatant c1, Combatant c2) {
                // Se c1 è Warrior, va prima (-1)
                if (isWarrior(c1)) return -1;
                // Se c2 è Warrior, c1 va dopo (1)
                if (isWarrior(c2)) return 1;
                // Altrimenti mantieni ordine di inserimento o usa altre logiche (es. velocità)
                return 0;
            }
        });

        // 4. AVVIO PRIMO TURNO
        this.turnIndex = 0;
        startTurn();
    }

    // Helper per verificare se è un Warrior
    private boolean isWarrior(Combatant c) {
        if (c instanceof PlayerCombatant) {
            // Assumi che PlayerCombatant esponga l'archetipo o lo recuperi dal sourceData
            return "Warrior".equalsIgnoreCase(((PlayerCombatant) c).getArchetype());
        }
        return false;
    }

    @Override
    protected BaseMenuScreen createView() {
        return new CombatScreen(this);
    }

    // --- COSTRUZIONE MENU DINAMICO ---
    public MenuComponent getDynamicCombatMenu() {
        MenuComposite root = new MenuComposite("ROOT");
        MenuComposite actions = new MenuComposite("AZIONI");

        for (Skill s : currentActor.getSkills()) {
            String label = s.getName();
            if (!s.isReady()) {
                label += " (" + s.getCurrentCooldown() + ")";
            }
            actions.add(new MenuItem(label, new UseSkillCommand(this, s)));
        }

        root.add(actions);
        return root;
    }

    // --- LOGICA GESTIONE TURNI AGGIORNATA ---
    private void startTurn() {
        this.currentActor = turnQueue.get(turnIndex);

        // 1. Tick cooldown e aggiornamento durata effetti
        this.currentActor.tickCooldowns();
        this.currentActor.updateEffects();

        if (currentActor.getCurrentHp() <= 0) {
            nextTurn();
            return;
        }

        // 2. CONTROLLO CONGELAMENTO: Se è stordito, salta il turno
        if (currentActor.isStunned()) {
            System.out.println("[TURN] " + currentActor.getName() + " è congelato e salta il turno!");
            nextTurn();
            return;
        }

        System.out.println("[TURN] Inizio turno: " + currentActor.getName());

        if (currentActor instanceof EnemyCombatant) {
            simulateEnemyTurn();
        }

        if (view instanceof CombatScreen) {
            ((CombatScreen) view).updateActionMenu();
        }
    }

    public void nextTurn() {
        turnIndex = (turnIndex + 1) % turnQueue.size();
        startTurn();
    }

    // --- RECEIVER METHODS ---
    public void executeSkillUsage(Skill skill) {
        if (!isPlayerTurn()) return;

        if (!skill.isReady()) {
            System.out.println("[WARNING] Abilità in ricarica!");
            return;
        }

        System.out.println("[ACTION] " + currentActor.getName() + " usa " + skill.getName());

        SystemFacade facade = game.getSystemFacade();
        facade.getAudioManager().playSound("music/sfx/battle/03_Claw_03.wav", facade.getAssetManager());

        // Target: Primo nemico
        Combatant target = enemies.get(0);

        skill.use(currentActor, target, getFullContext());

        if (!checkWinCondition()) {
            nextTurn();
        }
    }

    // --- AI NEMICA CON PROVOCAZIONE ---
    private void simulateEnemyTurn() {
        List<Combatant> aliveHeroes = heroes.stream()
            .filter(h -> h.getCurrentHp() > 0)
            .collect(Collectors.toList());

        if (aliveHeroes.isEmpty()) {
            checkLoseCondition();
            return;
        }

        Combatant target = null;

        // CONTROLLO PROVOCAZIONE: Il nemico deve colpire chi lo ha provocato
        for (StatusEffect e : currentActor.getActiveEffects()) {
            if ("TAUNT".equals(e.getType()) && e.getSource() != null && e.getSource().getCurrentHp() > 0) {
                target = e.getSource();
                System.out.println("[AI] Provocazione attiva! " + currentActor.getName() + " deve attaccare " + target.getName());
                break;
            }
        }

        // Se non è provocato, sceglie casuale
        if (target == null) {
            target = aliveHeroes.get(random.nextInt(aliveHeroes.size()));
        }

        System.out.println("[AI] " + currentActor.getName() + " attacca " + target.getName());

        // Audio ed esecuzione danno
        SystemFacade facade = game.getSystemFacade();
        facade.getAudioManager().playSound("music/sfx/battle/03_Claw_03.wav", facade.getAssetManager());

        target.takeDamage(currentActor.getAttackPower());

        if (!checkLoseCondition()) {
            nextTurn();
        }
    }

    private boolean checkWinCondition() {
        if (enemies.get(0).getCurrentHp() <= 0) {
            System.out.println("[COMBAT] VITTORIA!");
            this.specificEnemyData.setDefeated(true);

            // Calculate rewards from defeated enemy
            int xpGained = specificEnemyData.getXpReward();
            int karmaGained = specificEnemyData.getKarmaReward();

            System.out.println("[REWARDS] XP: " + xpGained + ", Karma: " + karmaGained);

            // Create reward object
            combatReward = new CombatReward(xpGained, karmaGained);

            // Check if defeated enemy is a boss
            if (specificEnemyData.getName() != null && specificEnemyData.getName().startsWith("Boss_L")) {
                System.out.println("[COMBAT] Boss defeated! Setting boss victory flag.");
                combatReward.setBossVictory(true);
            }

            // Distribute rewards to both players
            GameSession session = game.getCurrentSession();
            PlayerCharacter p1 = session.getP1();
            PlayerCharacter p2 = session.getP2();

            // Add XP and check for level ups
            boolean p1LeveledUp = p1.addExperience(xpGained);
            boolean p2LeveledUp = p2.addExperience(xpGained);

            if (p1LeveledUp) {
                combatReward.addLevelUp(p1.getName());
            }
            if (p2LeveledUp) {
                combatReward.addLevelUp(p2.getName());
            }

            // Add Karma
            p1.modifyKarma(karmaGained);
            p2.modifyKarma(karmaGained);

            // Transition to Victory Screen
            game.changeAppState(new VictoryState(combatReward));

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
            System.out.println("[COMBAT] SCONFITTA! Tutti gli eroi sono caduti.");

            // --- CORREZIONE US23: Transizione a Game Over ---
            game.changeAppState(new GameOverState());

            return true;
        }
        return false;
    }

    // --- GETTERS ---
    public Combatant getCurrentActor() { return currentActor; }
    public List<Combatant> getHeroes() { return heroes; }
    public List<Combatant> getEnemies() { return enemies; }

    public boolean isPlayerTurn() {
        return currentActor instanceof PlayerCombatant;
    }

    public CombatReward getCombatReward() {
        return combatReward;
    }
}

