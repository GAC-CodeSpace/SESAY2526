package io.SesProject.model.game.combat.skillsStrategy;

import io.SesProject.model.game.combat.Combatant;

import java.util.List;

public class AoeDamageStrategy implements SkillStrategy {
    private int mainDamage;
    private int sideDamage;

    public AoeDamageStrategy(int main, int side) {
        this.mainDamage = main;
        this.sideDamage = side;
    }

    @Override
    public void execute(Combatant user, Combatant target, List<Combatant> context) {
        target.takeDamage(mainDamage);
        for (Combatant c : context) {
            if (c != target) c.takeDamage(sideDamage);
        }
    }
}
