package io.SesProject.controller.state;

import io.SesProject.RpgGame;
import io.SesProject.controller.GameOverController;

public class GameOverState implements GameState {

    @Override
    public void enter(RpgGame game) {
        System.out.println("[STATE] Entering Game Over State");

        // 1. Suono/Musica drammatica (Opzionale)
        // game.getSystemFacade().getAudioManager().playMusic("music/gameover.ogg");

        // 2. Attiva il Controller specifico
        GameOverController controller = new GameOverController(game, game.getAuthService());
        game.changeController(controller);
    }

    @Override
    public void update(RpgGame game, float delta) {
        // Nessun update logico del mondo di gioco -> Gioco Bloccato
    }

    @Override
    public void exit(RpgGame game) {
        System.out.println("[STATE] Exiting Game Over State");
    }
}
