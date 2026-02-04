package io.SesProject.model.memento;

import java.util.ArrayList;
import java.util.List;

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
    public int karma;
    public float x, y;

    public List<ItemMemento> inventory = new ArrayList<>();
    public List<ItemMemento> equippedSkills = new ArrayList<>();

    public ItemMemento equippedWeapon;
    public ItemMemento equippedArmor;

    public PlayerCharacterMemento() {} // Costruttore vuoto per JSON


}
