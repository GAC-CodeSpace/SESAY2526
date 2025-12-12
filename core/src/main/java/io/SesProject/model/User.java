package io.SesProject.model;

import io.SesProject.model.memento.Memento;
import io.SesProject.model.memento.UserMemento;





public class User {

    private String accountName;
    private PlayerCharacter p1;
    private PlayerCharacter p2;

    public User() {}

    public User(String accountName) {
        this.accountName = accountName;
        // Valori di default
        this.p1 = new PlayerCharacter("Giocatore 1", "Warrior");
        this.p2 = new PlayerCharacter("Giocatore 2", "Mage");
    }

    // --- MEMENTO: SAVE (Delegato) ---
    public Memento save() {
        UserMemento m = new UserMemento();
        m.accountName = this.accountName;

        // DELEGAZIONE: User chiede a P1 e P2 di salvarsi da soli.
        // User non conosce i campi interni (hp, level) dei PlayerCharacter qui.
        if (this.p1 != null) m.player1 = this.p1.save();
        if (this.p2 != null) m.player2 = this.p2.save();

        return m;
    }

    // --- MEMENTO: RESTORE (Delegato) ---
    public void restore(Memento m) {
        if (!(m instanceof UserMemento)) throw new IllegalArgumentException("Memento errato");
        UserMemento state = (UserMemento) m;

        this.accountName = state.accountName;

        // DELEGAZIONE: Ricostruiamo gli oggetti e diciamo loro di ripristinarsi
        if (state.player1 != null) {
            // Se p1 non esiste lo creo, altrimenti lo aggiorno
            if (this.p1 == null) this.p1 = new PlayerCharacter();
            this.p1.restore(state.player1);
        }

        if (state.player2 != null) {
            if (this.p2 == null) this.p2 = new PlayerCharacter();
            this.p2.restore(state.player2);
        }

        System.out.println("Ripristino completato per account: " + accountName);
    }

    public String getUsername() { return accountName; }
    public PlayerCharacter getP1() { return p1; }
    public PlayerCharacter getP2() { return p2; }
}
