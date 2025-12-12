package io.SesProject.model.memento;

/**
 * Memento per il singolo personaggio.
 * Contiene lo stato interno (hp, level, pos) senza logica.
 */
public class PlayerCharacterMemento {
    public String name;
    public String archetype;
    public int level;
    public int hp;
    public int maxHp;
    public float x, y;

    public PlayerCharacterMemento() {} // Costruttore vuoto per JSON
}
