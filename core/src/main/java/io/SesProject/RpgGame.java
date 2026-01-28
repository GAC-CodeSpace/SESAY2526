package io.SesProject;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.SesProject.controller.BaseController;
import io.SesProject.controller.GameController;
import io.SesProject.controller.state.GameState;
import io.SesProject.controller.state.InitState;
import io.SesProject.model.GameSession;
import io.SesProject.model.SettingsService;
import io.SesProject.model.User;
import io.SesProject.service.AudioManager;
import io.SesProject.service.AuthService;
import io.SesProject.service.SaveService;
import io.SesProject.service.SystemFacade;


public class RpgGame extends Game {

    // Risorsa grafica condivisa
    public SpriteBatch batch;

    // --- SERVICES (Infrastruttura) ---
    private SystemFacade systemFacade;
    private AuthService authService; // Logica di Business per i profili

    // --- STATE PATTERN (Ciclo di vita App) ---
    private GameState currentAppState;

    // --- DATA CONTEXT (Stato del Gioco) ---
    private User currentUser;          // Il Profilo (es. "Mario") -> Cartella
    private GameSession currentSession; // La Partita (HP, LVL, Mappa) -> File specifico
    private GameController activeGameController;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // 1. Inizializza i Servizi
        this.systemFacade = new SystemFacade();
        this.authService = new AuthService();

        // 2. Avvia la macchina a stati (Inizia caricando gli asset)
        changeAppState(new InitState());
    }

    // --- GESTIONE STATO APPLICAZIONE (Init -> Play -> Exit) ---

    public void changeAppState(GameState newState) {
        if (currentAppState != null) {
            currentAppState.exit(this);
        }
        currentAppState = newState;
        currentAppState.enter(this);
    }

    @Override
    public void render() {
        // Aggiorna lo stato corrente (es. caricamento asset)
        if (currentAppState != null) {
            currentAppState.update(this, Gdx.graphics.getDeltaTime());
        }
        // Disegna la schermata corrente (View)
        super.render();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        // Nota: La pulizia profonda dei servizi avviene in ExitState tramite la Facade
    }

    // --- GESTIONE CONTROLLER (Navigazione UI) ---

    public void changeController(BaseController controller) {
        controller.show();
    }

    // --- GETTERS & SETTERS (Contesto Dati) ---

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            System.out.println("[GAME CONTEXT] Profilo attivo: " + user.getUsername());
        } else {
            System.out.println("[GAME CONTEXT] Logout profilo.");
            this.currentSession = null; // Se esce il profilo, chiudo la sessione
        }
    }

    public void setActiveGameController(GameController controller) {
        this.activeGameController = controller;
    }

    public GameController getActiveGameController() {
        return activeGameController;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentSession(GameSession session) {
        this.currentSession = session;
        if (session != null) {
            System.out.println("[GAME CONTEXT] Sessione di gioco caricata in memoria.");
        }
    }

    public GameSession getCurrentSession() {
        return currentSession;
    }

    // --- ACCESSO AI SERVIZI ---

    public SystemFacade getSystemFacade() {
        return systemFacade;
    }

    public AuthService getAuthService() {
        return authService;
    }

    // Metodi helper per retrocompatibilità con i Controller esistenti
    // (Delegando alla Facade, evitiamo di dover riscrivere tutti i controller ora)
    public SaveService getSaveService() {
        return systemFacade.getSaveService();
    }

    public SettingsService getSettingsService() {
        return systemFacade.getSettingsService();
    }

    public AudioManager getAudioManager() {
        return systemFacade.getAudioManager();
    }
}
