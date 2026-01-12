package io.SesProject.model;


import io.SesProject.model.memento.GameSessionMemento;
import io.SesProject.model.memento.Memento;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Rappresenta una specifica partita salvata (Slot).
 * È questo l'Originator del Pattern Memento per i salvataggi.
 */
public class GameSession {

    private PlayerCharacter p1;
    private PlayerCharacter p2;
    private String creationDate;

    // Costruttore "New Game": Inizializza tutto a default
    public GameSession() {
        this.p1 = new PlayerCharacter("Giocatore 1", "Warrior");
        this.p2 = new PlayerCharacter("Giocatore 2", "Mage");

        // Imposta la data corrente
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.creationDate = LocalDateTime.now().format(dtf);
    }

    // --- MEMENTO PATTERN: SAVE ---
    public Memento save() {
        GameSessionMemento m = new GameSessionMemento();
        m.creationDate = this.creationDate;

        // Delega ai personaggi
        if (p1 != null) m.player1 = p1.save();
        if (p2 != null) m.player2 = p2.save();

        return m;
    }

    // --- MEMENTO PATTERN: RESTORE ---
    public void restore(Memento m) {
        if (!(m instanceof GameSessionMemento)) return;
        GameSessionMemento state = (GameSessionMemento) m;

        this.creationDate = state.creationDate;

        if (this.p1 == null) this.p1 = new PlayerCharacter();
        this.p1.restore(state.player1);

        if (this.p2 == null) this.p2 = new PlayerCharacter();
        this.p2.restore(state.player2);
    }

    // Getters
    public String getCreationDate() { return creationDate; }

    public PlayerCharacter getP1() {
        return this.p1;
    }

    public PlayerCharacter getP2() {
        return this.p2;
    }
}
