package io.SesProject.controller;



import com.badlogic.gdx.Gdx;
import io.SesProject.RpgGame;
import io.SesProject.model.User;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.MainMenuScreen;


public class MainMenuController extends BaseController {

    public MainMenuController(RpgGame game, AuthService authService) {
        super(game, authService);
    }

    @Override
    protected BaseMenuScreen createView() {
        return new MainMenuScreen(this);
    }

    /**
     * Avvia il gioco (Passa alla GameScreen).
     */
    public void startGame() {
        System.out.println("Avvio del mondo di gioco...");
        // TODO: Prossimo passo -> Implementare GameController e GameScreen
        // game.changeController(new GameController(game, authService));

        // Per ora stampiamo solo un messaggio di debug
        System.out.println("Utente corrente: " + game.getCurrentUser().getUsername());

    }

    /**
     * Effettua il logout tornando alla schermata iniziale.
     */
    /**
     * Effettua il logout tornando alla schermata iniziale.
     */
    public void logout() {
        // Usiamo postRunnable per evitare conflitti durante il click del mouse
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                System.out.println("Esecuzione Logout...");

                // 1. Reset dell'utente
                game.setCurrentUser(null);

                // 2. Cambio schermata pulito
                game.changeController(new LoginController(game, authService));
            }
        });
    }

    /**
     * Chiude l'applicazione.
     */
    public void exitGame() {
        Gdx.app.exit();
    }
}
