package io.SesProject.model.game.combat.skillsStrategy;

import io.SesProject.model.game.combat.Combatant;
import java.util.List;

public interface SkillStrategy {
    // Aggiungiamo 'context' per vedere tutti i partecipanti alla battaglia
    void execute(Combatant user, Combatant target, List<Combatant> context);
}
