package io.SesProject;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.SesProject.controller.BaseController;
import io.SesProject.controller.LoginController;
import io.SesProject.model.User;
import io.SesProject.service.AuthService;
import io.SesProject.service.SaveService;

public class RpgGame extends Game {

    // Risorsa grafica condivisa per evitare di crearne una per ogni schermata
    // (Le Screen useranno game.batch per disegnare)
    public SpriteBatch batch;

    // Servizi principali (Singleton per la durata del gioco)
    private AuthService authService;
    private SaveService saveService;

    // Stato Globale: L'utente attualmente loggato
    private User currentUser;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // 1. Inizializziamo i servizi
        this.authService = new AuthService();
        this.saveService = new SaveService();

        // 2. Avviamo il gioco partendo dal Login
        // Creiamo il primo controller e gli passiamo il riferimento al gioco e all'auth
        LoginController startController = new LoginController(this, authService);

        // 3. Attiviamo il controller
        changeController(startController);
    }

    /**
     * Metodo chiave per l'architettura MVC proposta.
     * Riceve un nuovo Controller e delega a lui la creazione e visualizzazione della View.
     */
    public void changeController(BaseController controller) {
        // Il metodo show() del BaseController chiama createView() e poi setScreen()
        controller.show();
    }

    // --- Gestione Stato Utente ---
    public void setCurrentUser(User user) {
        this.currentUser = user;

        // MODIFICA QUI: Controlliamo se user è null prima di stampare
        if (user != null) {
            System.out.println("Utente corrente impostato: " + user.getUsername());
        } else {
            System.out.println("Utente disconnesso (Logout effettuato).");
        }
    }

    // ... resto del codice ...

    public User getCurrentUser() {
        return currentUser;
    }

    // --- Accesso ai Servizi ---

    public AuthService getAuthService() {
        return authService;
    }

    public SaveService getSaveService() {
        return saveService;
    }

    // --- Ciclo di vita LibGDX ---

    @Override
    public void render() {
        // Importante: delega il render alla Screen attiva (LoginScreen, GameScreen, ecc.)
        super.render();
    }

    @Override
    public void dispose() {
        // Pulizia risorse grafiche alla chiusura
        if (batch != null) batch.dispose();
        // Se la screen attiva ha risorse, super.dispose() potrebbe gestirle,
        // ma è buona norma gestire il dispose nelle singole schermate.
    }
}
