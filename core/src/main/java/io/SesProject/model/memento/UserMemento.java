package io.SesProject.model.memento;

/**
 * Memento per l'Account (User).
 * Aggrega i memento dei due giocatori.
 */
public class UserMemento implements Memento {
    public String accountName;

    // Contiene i "pacchetti" chiusi dei due eroi
    public PlayerCharacterMemento player1;
    public PlayerCharacterMemento player2;

    public UserMemento() {}
}
