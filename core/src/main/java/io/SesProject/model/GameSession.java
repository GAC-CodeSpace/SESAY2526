package io.SesProject.model;


import io.SesProject.model.game.movementStrategy.InputMovementStrategy;
import io.SesProject.model.game.movementStrategy.StaticStrategy;
import io.SesProject.model.game.npc.NpcData;
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

    public GameSession() {
        // ... init p1, p2, date ...
        this.worldNpcs = new ArrayList<>();
    }

    // --- SAVE ---
    public Memento save() {
        GameSessionMemento m = new GameSessionMemento();
        m.creationDate = this.creationDate;
        m.player1 = p1.save();
        m.player2 = p2.save();

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
            data.setMovementStrategy(new InputMovementStrategy());
        } else {
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
}
