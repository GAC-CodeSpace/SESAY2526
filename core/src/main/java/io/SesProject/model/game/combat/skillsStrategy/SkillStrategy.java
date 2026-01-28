package io.SesProject.model.game.combat.skillsStrategy;

import io.SesProject.model.game.combat.Combatant;

public interface SkillStrategy {
    void perform(Combatant user, Combatant target);
}
