package io.SesProject.controller;


import io.SesProject.RpgGame;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.RegisterScreen;

public class RegisterController extends BaseController {

    public RegisterController(RpgGame game, AuthService authService) {
        super(game, authService);
    }

    @Override
    protected BaseMenuScreen createView() {
        // Factory Method: restituisce la schermata di registrazione
        return new RegisterScreen(this);
    }

    /**
     * Tenta di registrare un nuovo utente.
     * @param username Il nome scelto
     */
    public void tryRegister(String username) {
        // Validazione base
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Errore: Username vuoto.");
            return;
        }

        // Chiamata al Model
        if (authService.register(username)) {
            System.out.println("Registrazione avvenuta con successo: " + username);
            // Dopo la registrazione, torniamo al Login per far caricare i dati
            backToLogin();
        } else {
            System.out.println("Errore: Utente già esistente.");
            // Qui potresti notificare la view per mostrare un messaggio rosso
        }
    }

    /**
     * Torna alla schermata di Login (Annulla o Successo)
     */
    public void backToLogin() {
        game.changeController(new LoginController(game, authService));
    }
}
