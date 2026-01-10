package io.SesProject.controller;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.SesProject.RpgGame;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.gameScenes.GameScreen;


public class GameController extends BaseController {

    public GameController(RpgGame game, AuthService authService) {
        super(game, authService);
        // Qui in futuro verrà avviata la musica specifica del livello
        // game.getSystemFacade().getAudioManager().playMusic("music/dungeon.ogg");
    }

    @Override
    protected BaseMenuScreen createView() {
        // FACTORY METHOD: Restituisce la View di gioco
        return new GameScreen(this);
    }

    public void handleInput(float delta) {
        // Se premo ESC, cambio controller verso il Menu di Pausa
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("[GAME] Apertura Menu Pausa...");
            game.changeController(new PauseMenuController(game, authService));
        }
    }
}
