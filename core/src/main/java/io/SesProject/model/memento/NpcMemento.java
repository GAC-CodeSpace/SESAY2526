package io.SesProject.model.memento;

/**
 * Snapshot dello stato di un singolo NPC.
 */
public class NpcMemento {
    // Dati identità (per ricostruirlo)
    public String name;
    public String spriteName;
    public boolean isHostile;
    public String dialogue;

    // Dati di stato (quelli che cambiano)
    public float x, y;
    public int currentHp;
    public int maxHp;
    public int attackPower;
    public boolean isDefeated;

    public NpcMemento() {}
}
