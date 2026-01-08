package io.SesProject.controller.state;


import io.SesProject.RpgGame;
import io.SesProject.controller.LoginController;

public class PlayState implements GameState {

    @Override
    public void enter(RpgGame game) {
        System.out.println("[STATE] Entering PlayState");
        game.changeController(new LoginController(game, game.getAuthService()));
    }

    @Override
    public void update(RpgGame game, float delta) {
        // Niente di specifico qui, il rendering è gestito dalle Screen di LibGDX
    }

    @Override
    public void exit(RpgGame game) {
        System.out.println("[STATE] Exiting PlayState");
    }
}
