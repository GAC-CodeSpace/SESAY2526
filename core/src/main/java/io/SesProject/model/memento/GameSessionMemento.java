package io.SesProject.model.memento;

import java.util.ArrayList;
import java.util.List;

/*--CONCRETE MEMENTO*/
public class GameSessionMemento implements Memento {
    public String creationDate;

    // I dati dei personaggi stanno qui, NON nel profilo utente
    public PlayerCharacterMemento player1;
    public PlayerCharacterMemento player2;
    public List<NpcMemento> npcs = new ArrayList<>();

    public GameSessionMemento() {}
}
