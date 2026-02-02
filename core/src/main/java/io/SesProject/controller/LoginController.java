package io.SesProject.controller;


import io.SesProject.RpgGame;
import io.SesProject.model.User;
import io.SesProject.service.AuthService;
import io.SesProject.service.SystemFacade;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.LoginScreen;

public class LoginController extends BaseController {

    // Nota: Non serve più SaveService qui!

    public LoginController(RpgGame game, AuthService authService) {
        super(game, authService);
        game.getSystemFacade().getAudioManager().playMusic("music/AdhesiveWombat-Night Shade.mp3");

    }

    @Override
    protected BaseMenuScreen createView() {
        return new LoginScreen(this);
    }

    public void tryLogin(String username) {
        // 1. Verifica esistenza (AuthService controlla se esiste la cartella)
        if (authService.login(username)) {
            System.out.println("[AUTH] Login OK. Profilo selezionato: " + username);

            // 2. Creazione Identità (Senza caricare dati di gioco!)
            // Creiamo un oggetto User che contiene SOLO il nome.
            User currentUser = new User(username);

            // 3. Imposta l'identità nel contesto globale
            game.setCurrentUser(currentUser);

            SystemFacade facade = game.getSystemFacade();

            facade.getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav" , facade.getAssetManager());

            // 4. Vai al Main Menu
            game.changeController(new MainMenuController(game, authService));
        } else {
            String msg = "Errore, profilo non trovato, riprova.";
            if(view instanceof LoginScreen){
                ((LoginScreen) view).showMessage("FALLIMENTO" , msg);
            }
            System.out.println("[AUTH] Errore: Profilo non trovato.");
        }
    }

    public void goToRegister() {
        SystemFacade facade = game.getSystemFacade();

        facade.getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav" , facade.getAssetManager());
        game.changeController(new RegisterController(game, authService));
    }
}
