package io.SesProject.model.game.combat.combatObs;

import io.SesProject.model.game.combat.Combatant;
/*OBSERVER INTERFACE PATTERN
* Definisce il contratto per chi vuole ascoltare i cambiamenti di un combattente*/



public interface CombatantObserver {
    void onHealthChanged(Combatant source);
    void onDeath(Combatant source);
}
