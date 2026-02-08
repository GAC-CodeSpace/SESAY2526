package io.SesProject.model.game.combat.skillsStrategy;

import io.SesProject.model.game.combat.Combatant;

import java.util.List;

public interface SkillStrategy {
    void execute(Combatant user, Combatant target, List<Combatant> context);
}
