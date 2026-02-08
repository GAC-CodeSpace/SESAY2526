package io.SesProject.model.game.combat.skillsStrategy;

import io.SesProject.model.game.combat.Combatant;

import java.util.List;

public class DrainStrategy implements SkillStrategy {
    @Override
    public void execute(Combatant user, Combatant target, List<Combatant> context) {
        int damage = user.getAttackPower();
        target.takeDamage(damage);
        // Cura l'alleato (cerca nel contesto qualcuno che non sia il nemico)
        for (Combatant c : context) {
            if (c != target && c != user) c.heal(damage / 2);
        }
    }
}
