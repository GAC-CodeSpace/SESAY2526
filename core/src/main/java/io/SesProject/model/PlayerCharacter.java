package io.SesProject.model;

import io.SesProject.model.memento.PlayerCharacterMemento;

public class PlayerCharacter {

    private String name;
    private String archetype;
    private int level;
    private int hp;
    private int maxHp;
    private float x, y;

    public PlayerCharacter() {} // Per JSON interno se servisse

    public PlayerCharacter(String name, String archetype) {
        this.name = name;
        this.archetype = archetype;
        this.level = 1;
        this.maxHp = 100;
        this.hp = 100;
        this.x = 0;
        this.y = 0;
    }

    // --- MEMENTO: SAVE (Locale) ---
    // Crea lo snapshot solo per questo oggetto
    public PlayerCharacterMemento save() {
        PlayerCharacterMemento m = new PlayerCharacterMemento();
        m.name = this.name;
        m.archetype = this.archetype;
        m.level = this.level;
        m.hp = this.hp;
        m.maxHp = this.maxHp;
        m.x = this.x;
        m.y = this.y;
        return m;
    }

    // --- MEMENTO: RESTORE (Locale) ---
    // Ripristina lo stato interno dai dati ricevuti
    public void restore(PlayerCharacterMemento m) {
        if (m == null) return;
        this.name = m.name;
        this.archetype = m.archetype;
        this.level = m.level;
        this.hp = m.hp;
        this.maxHp = m.maxHp;
        this.x = m.x;
        this.y = m.y;
    }

    // Getters e Setters di gioco
    public String getName() { return name; }
    public int getLevel() { return level; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return y;
    }


}
