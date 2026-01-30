package io.SesProject.model.game.item.observer;

import io.SesProject.model.PlayerCharacter;

/**
 * OBSERVER INTERFACE.
 * Definisce il metodo di aggiornamento che i Concrete Observer devono implementare.
 */
public interface PlayerStatsObserver {
    // Chiamato dal Subject quando lo stato (stats o equipaggiamento) cambia.
    void onStatsChanged(PlayerCharacter subject);
}
