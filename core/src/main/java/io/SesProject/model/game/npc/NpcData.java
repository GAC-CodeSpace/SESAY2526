package io.SesProject.model.game.npc;

import io.SesProject.model.game.movementStrategy.MovementStrategy;
import io.SesProject.model.game.movementStrategy.StaticStrategy;
import io.SesProject.model.memento.NpcMemento;

/*Tale classe definisce i parametri di configurazione degli npc*/

public class NpcData {
    // Identità
    private String name;
    private String spriteName;

    // Logica
    private boolean isHostile;
    private boolean isDefeated = false;
    private String dialogue;

    // Fisica e Comportamento
    private float x, y;
    private int maxHp;
    private int currentHp;
    private int attackPower;
    private MovementStrategy movementStrategy;

    private int xpReward;
    private int karmaReward;

    public NpcData() {
        // Valori di default
        this.movementStrategy = new StaticStrategy();
        this.isHostile = false;
    }

    // --- MEMENTO: SAVE ---
    public NpcMemento save() {
        NpcMemento m = new NpcMemento();
        m.name = this.name;
        m.spriteName = this.spriteName;
        m.isHostile = this.isHostile;
        m.dialogue = this.dialogue;
        m.x = this.x;
        m.y = this.y;
        m.currentHp = this.currentHp;
        m.maxHp = this.maxHp;
        m.attackPower = this.attackPower;
        m.isDefeated = this.isDefeated; // Fondamentale!
        m.xpReward = this.xpReward;
        m.karmaReward = this.karmaReward;
        return m;
    }

    // --- MEMENTO: RESTORE ---
    public void restore(NpcMemento m) {
        this.name = m.name;
        this.spriteName = m.spriteName;
        this.isHostile = m.isHostile;
        this.dialogue = m.dialogue;
        this.x = m.x;
        this.y = m.y;
        this.currentHp = m.currentHp;
        this.maxHp = m.maxHp;
        this.attackPower = m.attackPower;
        this.isDefeated = m.isDefeated;
        this.xpReward = m.xpReward;
        this.karmaReward = m.karmaReward;
    }

    // Setters (usati dal Builder)
    public void setName(String name) { this.name = name; }
    public void setSpriteName(String spriteName) { this.spriteName = spriteName; }
    public void setHostile(boolean hostile) { isHostile = hostile; }
    public void setDialogue(String dialogue) { this.dialogue = dialogue; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
        this.currentHp = maxHp; // Default full HP
    }

    public void setCurrentHp(int hp) { this.currentHp = hp; }
    public void setMovementStrategy(MovementStrategy behavior) { this.movementStrategy = behavior; }
    public boolean isDefeated() { return isDefeated; }
    public void setDefeated(boolean defeated) { isDefeated = defeated; }
    public void setAttackPower(int power) { this.attackPower = power; }

    // Getters (usati dalle Entità di Gioco)
    public String getName() { return name; }
    public String getSpriteName() { return spriteName; }
    public boolean isHostile() { return isHostile; }
    public String getDialogue() { return dialogue; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public MovementStrategy getMovementStrategy() { return movementStrategy; }
    public int getAttackPower() { return attackPower; }
    public int getXpReward() { return xpReward; }
    public int getKarmaReward() { return karmaReward; }
    public void setXpReward(int xp) { this.xpReward = xp; }
    public void setKarmaReward(int karma) { this.karmaReward = karma; }
}
