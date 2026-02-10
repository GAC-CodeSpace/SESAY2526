package io.SesProject.controller.state;

import io.SesProject.RpgGame;
import io.SesProject.controller.PauseMenuController;

public class PausedState implements GameState {

    @Override
    public void enter(RpgGame game) {
        System.out.println("[STATE] Entering PausedState");

        // 1. Mostriamo il Menu di Pausa
        // Nota: Passiamo authService che è disponibile in RpgGame
        game.changeController(new PauseMenuController(game, game.getAuthService()));
    }

    @Override
    public void update(RpgGame game, float delta) {
        // In PlayState, chiamavamo gameController.update().
        // Qui NON lo chiamiamo. Quindi nemici, fisica e input di movimento sono FERMI.

        // Disegniamo solo la schermata corrente (il menu di pausa)
        // (gestito automaticamente da game.render() che chiama super.render())
    }

    @Override
    public void exit(RpgGame game) {
        System.out.println("[STATE] Exiting PausedState");

    }
}
