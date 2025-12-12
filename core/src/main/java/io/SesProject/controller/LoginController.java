package io.SesProject.controller;


import io.SesProject.RpgGame;
import io.SesProject.model.memento.Memento;
import io.SesProject.model.User;
import io.SesProject.service.AuthService;
import io.SesProject.service.SaveService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.LoginScreen;





public class LoginController extends BaseController {

    private SaveService saveService;

    public LoginController(RpgGame game, AuthService authService) {
        super(game, authService);
        // Recuperiamo il servizio di salvataggio dal gioco principale
        this.saveService = game.getSaveService();
    }

    @Override
    protected BaseMenuScreen createView() {
        // Factory Method: restituisce la View specifica per questo controller
        return new LoginScreen(this);
    }

    /**
     * Tenta il login.
     * 1. Verifica esistenza file.
     * 2. Carica i dati (Memento).
     * 3. Ripristina l'utente.
     * 4. Passa al Menu Principale.
     */
    public void tryLogin(String username) {
        if (authService.login(username)) {
            System.out.println("Login OK: " + username + ". Caricamento dati...");

            // 1. Creiamo un utente "vuoto" con quel nome
            User loadedUser = new User(username);

            // 2. Usiamo il SaveService per caricare il Memento dal JSON
            Memento memento = saveService.loadGame(username);

            // 3. Se il salvataggio esiste ed è valido, ripristiniamo lo stato
            if (memento != null) {
                loadedUser.restore(memento);
            }

            // 4. Impostiamo l'utente nel contesto globale del gioco
            game.setCurrentUser(loadedUser);

            // 5. Cambio contesto -> Andiamo al Menu Principale

            game.changeController(new MainMenuController(game, authService));

        } else {
            System.out.println("Errore Login: Utente non trovato.");
            // Qui potresti chiamare un metodo sulla view per mostrare un popup di errore
            // ((LoginScreen)view).showError("Utente non trovato");
        }
    }

    /**
     * Naviga verso la schermata di registrazione
     */
    public void goToRegister() {
        game.changeController(new RegisterController(game, authService));
    }
}
