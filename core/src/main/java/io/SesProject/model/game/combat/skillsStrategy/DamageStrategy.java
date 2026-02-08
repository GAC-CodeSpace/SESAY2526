package io.SesProject.model.game.combat.skillsStrategy;

import io.SesProject.model.game.combat.Combatant;

import java.util.List;

public class DamageStrategy implements SkillStrategy {
    private int damage;
    public DamageStrategy(int damage) { this.damage = damage; }

    @Override
    public void execute(Combatant user, Combatant target, List<Combatant> context) {
        target.takeDamage(damage);
    }
}
