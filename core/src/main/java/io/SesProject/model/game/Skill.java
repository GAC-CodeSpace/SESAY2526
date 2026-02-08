package io.SesProject.model.game;


import io.SesProject.model.game.combat.Combatant;
import io.SesProject.model.game.combat.skillsStrategy.SkillStrategy;

import java.util.List;

public class Skill {
    private String name;
    private int maxCooldown;     // Turni di attesa (0 = sempre usabile)
    private int currentCooldown; // Turni rimanenti

    private SkillStrategy behavior; // Strategy

    public Skill(String name, int cooldown, SkillStrategy behavior) {
        this.name = name;
        this.maxCooldown = cooldown;
        this.currentCooldown = 0;
        this.setSkillStrategy(behavior);
    }

    public boolean isReady() {
        return currentCooldown <= 0;
    }

    public void use(Combatant user, Combatant target, List<Combatant> context) {
        if (behavior != null) {
            behavior.execute(user, target, context);
        }
        this.currentCooldown = maxCooldown;
    }

    public void reduceCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }

    public void setSkillStrategy(SkillStrategy behavior){
        this.behavior = behavior;
    }

    public String getName() { return name; }
    public int getCurrentCooldown() { return currentCooldown; }
}
