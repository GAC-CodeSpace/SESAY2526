package io.SesProject.controller;


import io.SesProject.RpgGame;
import io.SesProject.controller.command.*;
import io.SesProject.controller.enumsContainer.MenuSource;
import io.SesProject.controller.state.ExitState;
import io.SesProject.model.GameSession;
import io.SesProject.model.memento.Memento;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.model.menu.MenuComposite;
import io.SesProject.model.menu.MenuItem;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.MainMenuScreen;

public class MainMenuController extends BaseController {

    public MainMenuController(RpgGame game, AuthService authService) {
        super(game, authService);
        game.getSystemFacade().getAudioManager().playMusic("music/AdhesiveWombat-Night Shade.mp3");
    }

    @Override
    protected BaseMenuScreen createView() {
        // Factory Method: crea la View e gli passa l'albero del menu
        return new MainMenuScreen(this, buildMenuTree());
    }

    /**
     * Costruisce la struttura del menu (Pattern Composite).
     */
    private MenuComponent buildMenuTree() {
        MenuComposite root = new MenuComposite("MENU PRINCIPALE");

        // Associa ogni voce al relativo Comando
        root.add(new MenuItem("NUOVA PARTITA", new NewGameCommand(this)));
        root.add(new MenuItem("CARICA PARTITA", new LoadGameCommand(this)));
        root.add(new MenuItem("IMPOSTAZIONI", new OpenSettingsCommand(this)));
        root.add(new MenuItem("LOGOUT" , new LogOutCommand(this)));
        root.add(new MenuItem("ESCI DAL GIOCO", new ExitGameCommand(this)));

        return root;
    }

    // --- RECEIVER METHODS (Eseguiti dai Comandi) ---

    /**
     * LOGICA NEW GAME (Richiesta Specifica):
     * 1. Recupera il profilo corrente.
     * 2. Crea una nuova sessione di gioco (livello 1, data, ecc.).
     * 3. Salva immediatamente il file su disco in un nuovo slot.
     * 4. NON avvia la partita (rimane nel menu).
     */
    public void newGame() {
        System.out.println("[CMD] Richiesta creazione nuovo salvataggio...");

        // 1. Identifica l'utente corrente (Profilo caricato al login)
        String currentProfile = game.getCurrentUser().getUsername();

        // 2. FRESH INSTANCE (Prototype concettuale):
        // Crea una nuova GameSession (qui nascono P1 e P2 a livello 1 con la data odierna)
        GameSession initialSession = new GameSession();

        // 3. MEMENTO: Cattura lo stato iniziale (inclusa la data di creazione)
        Memento snapshot = initialSession.save();

        // 4. CARETAKER: Chiede al servizio di persistere il memento in un NUOVO slot
        // Usa la Facade per accedere al SaveService
        int newSlotId = game.getSystemFacade().getSaveService()
            .createNewSaveSlot(snapshot, currentProfile);

        if (newSlotId != -1) {
            // Successo
            String msg = "Nuova partita creata nello Slot " + newSlotId + ".\nVai su 'CARICA PARTITA' per giocare.";

            // Dobbiamo fare un cast perché 'view' in BaseController è generica
            if (view instanceof MainMenuScreen) {
                ((MainMenuScreen) view).showMessage("SUCCESSO", msg);
            }

            System.out.println("[UI] " + msg);
        } else {
            // Errore
            String errorMsg = "Errore durante la creazione del file.\nControlla i permessi o lo spazio su disco.";

            if (view instanceof MainMenuScreen) {
                ((MainMenuScreen) view).showMessage("ERRORE", errorMsg);
            }

            System.err.println("[UI] " + errorMsg);
        }

        // 5. Feedback visuale in console (o popup futuro)
        System.out.println("[UI] Partita creata con successo nello Slot " + newSlotId + ".");
        System.out.println("[UI] Ora puoi premere 'CARICA PARTITA' per giocare.");
    }


    public void loadGame() {
        System.out.println("[CMD] Load Game Selezionato - Apro lista salvataggi");
        // Cambio Controller -> LoadMenuController
        game.changeController(new LoadGameController(game, authService));
    }


    public void goToSettings() {
        System.out.println("[CMD] Settings Selezionato (da Main)");
        // Passiamo la sorgente
        game.changeController(new SettingsController(game, authService, MenuSource.MAIN_MENU));
    }

    public void logout(){
        System.out.println("[CMD] vado al login");
        game.changeController(new LoginController(game , authService));
    }

    public void exitGame() {
        System.out.println("[CMD] Exit Selezionato -> Transizione a ExitState");
        // Cambio stato per chiusura sicura (Pattern State)
        game.changeAppState(new ExitState());
    }
}
