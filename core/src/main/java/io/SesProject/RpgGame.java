package io.SesProject;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.SesProject.controller.BaseController;
import io.SesProject.controller.LoginController;
import io.SesProject.model.SettingsService;
import io.SesProject.model.User;
import io.SesProject.model.observer.SettingsHandler;
import io.SesProject.service.AudioManager;
import io.SesProject.service.AuthService;
import io.SesProject.service.SaveService;

public class RpgGame extends Game {

    // Risorsa grafica condivisa per evitare di crearne una per ogni schermata
    // (Le Screen useranno game.batch per disegnare)
    public SpriteBatch batch;

    // Servizi principali
    private AuthService authService;
    private SaveService saveService;
    private SettingsService settingsService;
    private AudioManager audioManager;

    // Stato Globale: L'utente attualmente loggato
    private User currentUser;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // 1. Inizializzazione Servizi
        this.authService = new AuthService();
        this.saveService = new SaveService();
        this.settingsService = new SettingsService();

        // Crea l'Audio Manager
        this.audioManager = new AudioManager();

        // 2. Setup Observer (Handler)
        // Ora l'Handler riceve l'AudioManager invece di 'this'
        SettingsHandler handler = new SettingsHandler(audioManager);
        this.settingsService.addObserver(handler);

        // 3. Applica Impostazioni Iniziali
        // Questo farà scattare onVolumeChanged -> audioManager.setMasterVolume()
        this.settingsService.applySettings();

        // 4. Avvia Musica di Sottofondo
        // La logica è incapsulata nel manager
        this.audioManager.playMusic("music/AdhesiveWombat-Night Shade.mp3");

        // 5. Avvia il gioco
        changeController(new LoginController(this, authService));
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
    public SettingsService getSettingsService() {
        return settingsService;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    // --- Ciclo di vita LibGDX ---

    @Override
    public void render() {
        // Importante: delega il render alla Screen attiva (LoginScreen, GameScreen, ecc.)
        super.render();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (audioManager != null) audioManager.dispose(); // Pulizia audio
    }
}
