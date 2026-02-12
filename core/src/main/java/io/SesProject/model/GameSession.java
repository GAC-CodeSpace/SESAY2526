package io.SesProject.model;


import io.SesProject.model.game.GameObject;
import io.SesProject.model.game.movementStrategy.InputMovementStrategy;
import io.SesProject.model.game.movementStrategy.RandomMovementStrategy;
import io.SesProject.model.game.movementStrategy.StaticStrategy;
import io.SesProject.model.game.npc.NpcData;
import io.SesProject.model.game.npc.factory.NpcEntity;
import io.SesProject.model.memento.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una specifica partita salvata (Slot).
 * È questo l'Originator del Pattern Memento per i salvataggi.
 */


import io.SesProject.model.memento.GameSessionMemento;
import io.SesProject.model.memento.Memento;


public class GameSession {

    private PlayerCharacter p1;
    private PlayerCharacter p2;
    private String creationDate;

    // NUOVO: La lista persistente degli NPC nel mondo
    private List<NpcData> worldNpcs;

    // Current map name to preserve map state
    private String currentMapName = "casa/casa.tmx"; // Default starting map

    private int saveSlotId = -1; // -1 = nuovo/non salvato, >0 = slot esistente

    private boolean isNewGame = false; // Flag to trigger story introduction

    public GameSession(boolean isP1Tank) {
        this.worldNpcs = new ArrayList<>();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.creationDate = LocalDateTime.now().format(dtf);

        // Usa la tua scelta per decidere le classi
        if (isP1Tank) {
            this.p1 = new PlayerCharacter("Giocatore 1", "Warrior");
            this.p2 = new PlayerCharacter("Giocatore 2", "Mage");
        } else {
            // Scambio dei ruoli!
            this.p1 = new PlayerCharacter("Giocatore 1", "Mage");
            this.p2 = new PlayerCharacter("Giocatore 2", "Warrior");
        }

        this.p2.setPosition(250, 100);
        this.p1.setPosition(100, 100);
    }

    public GameSession() {
        this(true); // Di default P1 è Tank
    }

    public void updateNpcsFromWorld(List<GameObject> worldEntities) {
        // DON'T clear the list - we need to preserve defeated NPCs!
        // Instead, update existing NPCs and add new ones

        // First, collect all NPCs currently in the world
        List<NpcData> npcsInWorld = new ArrayList<>();
        for (GameObject obj : worldEntities) {
            if (obj instanceof NpcEntity) {
                npcsInWorld.add(((NpcEntity) obj).getData());
            }
        }

        // Update existing NPCs or add new ones
        for (NpcData worldNpc : npcsInWorld) {
            // Check if this NPC already exists in our list
            boolean found = false;
            for (int i = 0; i < worldNpcs.size(); i++) {
                NpcData existingNpc = worldNpcs.get(i);
                // Match by name and approximate position
                if (existingNpc.getName().equals(worldNpc.getName()) &&
                    Math.abs(existingNpc.getX() - worldNpc.getX()) < 5 &&
                    Math.abs(existingNpc.getY() - worldNpc.getY()) < 5) {
                    // Update the existing NPC data (position may have changed)
                    worldNpcs.set(i, worldNpc);
                    found = true;
                    break;
                }
            }

            // If not found, add as new NPC
            if (!found) {
                worldNpcs.add(worldNpc);
            }
        }

        System.out.println("[SESSION] Dati NPC aggiornati per il salvataggio. Totale: " + worldNpcs.size());
    }
    // --- SAVE ---
    public Memento save() {
        GameSessionMemento m = new GameSessionMemento();
        m.creationDate = this.creationDate;
        m.player1 = p1.save();
        m.player2 = p2.save();
        m.currentMapName = this.currentMapName; // Save current map
        m.isNewGame = this.isNewGame; // Save new game flag

        // Salviamo lo stato di tutti gli NPC
        for (NpcData npc : worldNpcs) {
            m.npcs.add(npc.save());
        }

        return m;
    }

    // --- RESTORE ---
    public void restore(Memento m) {
        if (!(m instanceof GameSessionMemento)) return;
        GameSessionMemento state = (GameSessionMemento) m;

        this.creationDate = state.creationDate;
        this.currentMapName = state.currentMapName != null ? state.currentMapName : "casa/casa.tmx"; // Restore map or use default
        this.isNewGame = state.isNewGame; // Restore new game flag

        // Restore Players
        if (this.p1 == null) this.p1 = new PlayerCharacter();
        this.p1.restore(state.player1);
        if (this.p2 == null) this.p2 = new PlayerCharacter();
        this.p2.restore(state.player2);

        // Restore NPC
        this.worldNpcs.clear();
        if (state.npcs != null) {
            for (NpcMemento npcMem : state.npcs) {
                NpcData data = new NpcData();
                data.restore(npcMem);
                // IMPORTANTE: Dobbiamo riassegnare le strategie di default
                // perché il JSON non salva il codice (MovementBehavior).
                // Per ora assegniamo una strategia base (es. Static o Input se ostile)
                // In una versione avanzata, il Memento dovrebbe salvare il "tipo" di strategia.
                reassignStrategyBehavior(data);

                this.worldNpcs.add(data);
            }
        }
    }

    // Metodo helper per ripristinare il comportamento (limitazione della serializzazione)
    private void reassignStrategyBehavior(NpcData data) {
        if (data.isHostile()) {
            // Hostile NPCs (enemies) should move autonomously
            data.setMovementStrategy(new RandomMovementStrategy());
        } else {
            // Friendly NPCs (villagers, merchants) should stay in place
            data.setMovementStrategy(new StaticStrategy());
        }
    }

    public List<NpcData> getWorldNpcs() { return worldNpcs; }
    public void addNpc(NpcData npc) { this.worldNpcs.add(npc); }

    public PlayerCharacter getP1() {
        return this.p1;
    }

    public PlayerCharacter getP2(){
        return this.p2;
    }

    public void setSaveSlotId(int id) { this.saveSlotId = id; }
    public int getSaveSlotId() { return saveSlotId; }

    public String getCurrentMapName() { return currentMapName; }
    public void setCurrentMapName(String mapName) { this.currentMapName = mapName; }

    public boolean isNewGame() { return isNewGame; }
    public void setIsNewGame(boolean isNewGame) { this.isNewGame = isNewGame; }
}

