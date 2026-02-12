package io.SesProject.model.game.combat;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the rewards gained from a combat encounter.
 * Used to pass data from CombatController to VictoryScreen.
 */
public class CombatReward {
    private int totalXp;
    private int totalKarma;
    private List<String> levelUps; // Names of players who leveled up
    private boolean isBossVictory; // Flag to indicate boss was defeated

    public CombatReward() {
        this.levelUps = new ArrayList<>();
    }

    public CombatReward(int xp, int karma) {
        this.totalXp = xp;
        this.totalKarma = karma;
        this.levelUps = new ArrayList<>();
        this.isBossVictory = false;
    }

    public void addLevelUp(String playerName) {
        levelUps.add(playerName);
    }

    // Getters and Setters
    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int xp) { this.totalXp = xp; }

    public int getTotalKarma() { return totalKarma; }
    public void setTotalKarma(int karma) { this.totalKarma = karma; }

    public List<String> getLevelUps() { return levelUps; }

    public boolean hasLevelUps() { return !levelUps.isEmpty(); }

    public boolean isBossVictory() { return isBossVictory; }
    public void setBossVictory(boolean isBossVictory) { this.isBossVictory = isBossVictory; }
}
