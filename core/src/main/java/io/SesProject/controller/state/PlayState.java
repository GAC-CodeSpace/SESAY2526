package io.SesProject.controller.state;


import io.SesProject.RpgGame;
import io.SesProject.controller.GameController;
import io.SesProject.controller.LoginController;

public class PlayState implements GameState {

    @Override
    public void enter(RpgGame game) {
        System.out.println("[STATE] Entering PlayState");

        // CHECK INTELLIGENTE:
        // Se abbiamo una sessione attiva (cioè stiamo tornando dalla pausa o abbiamo caricato un gioco),
        // andiamo al GameController.
        if (game.getCurrentSession() != null) {
            System.out.println("[LOGIC] Ripresa sessione attiva -> GameController");
            // Nota: L'AudioManager gestirà la musica (non la riavvia se è la stessa)
            //game.getSystemFacade().getAudioManager().playMusic("music/field_theme.ogg");
            game.changeController(new GameController(game, game.getAuthService()));
        }
        // Altrimenti, se è un avvio "a freddo", andiamo al Login
        else {
            System.out.println("[LOGIC] Nessuna sessione -> LoginController");
            //game.getSystemFacade().getAudioManager().playMusic("music/theme.ogg");
            game.changeController(new LoginController(game, game.getAuthService()));
        }
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
