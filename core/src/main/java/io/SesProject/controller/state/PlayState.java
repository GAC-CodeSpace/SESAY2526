package io.SesProject.controller.state;

import io.SesProject.RpgGame;
import io.SesProject.controller.GameController;
import io.SesProject.controller.LoginController;

public class PlayState implements GameState {

    @Override
    public void enter(RpgGame game) {
        System.out.println("[STATE] Entering PlayState");

        // Se c'è una sessione attiva (cioè stiamo giocando)
        if (game.getCurrentSession() != null) {

            // --- CORREZIONE FONDAMENTALE ---
            // Controlliamo se abbiamo già un controller avviato per questa sessione.

            if (game.getActiveGameController() == null) {
                // Primo avvio: Creiamo il controller e lo salviamo in cache
                System.out.println("[SYSTEM] Creazione nuovo GameController...");
                GameController newController = new GameController(game, game.getAuthService());
                game.setActiveGameController(newController);
            } else {
                System.out.println("[SYSTEM] Ripristino GameController esistente.");
            }

            // Usiamo il controller (nuovo o recuperato dalla cache)
            // Avvia la musica di esplorazione (poiché il CombatState l'aveva cambiata)
            game.getSystemFacade().getAudioManager().playMusic("music/exploration_music.wav");

            game.changeController(game.getActiveGameController());

        } else {
            // Nessuna sessione -> Login
            game.getSystemFacade().getAudioManager().playMusic("music/AdhesiveWombat-Night Shade.mp3");
            game.changeController(new LoginController(game, game.getAuthService()));
        }
    }

    @Override
    public void update(RpgGame game, float delta) {}

    @Override
    public void exit(RpgGame game) {}
}
