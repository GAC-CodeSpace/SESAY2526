package io.SesProject.controller;


import io.SesProject.RpgGame;
import io.SesProject.controller.command.menuCommand.*;
import io.SesProject.controller.enumsContainer.MenuSource;
import io.SesProject.controller.state.ExitState;
import io.SesProject.model.GameSession;
import io.SesProject.model.memento.Memento;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.model.menu.MenuComposite;
import io.SesProject.model.menu.MenuItem;
import io.SesProject.service.AuthService;
import io.SesProject.service.SystemFacade;
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
        System.out.println("[UI] Apertura selezione ruoli...");
        game.getSystemFacade().getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav", game.getSystemFacade().getAssetManager());

        // Passa al nuovo controller per la scelta dei ruoli
        game.changeController(new RoleSelectionController(game, authService));
    }


    public void loadGame() {
        System.out.println("[CMD] Load Game Selezionato - Apro lista salvataggi");

        SystemFacade facade = game.getSystemFacade();

        facade.getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav" , facade.getAssetManager());
        // Cambio Controller -> LoadMenuController
        game.changeController(new LoadGameController(game, authService));
    }


    public void goToSettings() {
        System.out.println("[CMD] Settings Selezionato (da Main)");

        SystemFacade facade = game.getSystemFacade();

        facade.getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav" , facade.getAssetManager());
        // Passiamo la sorgente
        game.changeController(new SettingsController(game, authService, MenuSource.MAIN_MENU));
    }

    public void logout(){
        System.out.println("[CMD] vado al login");
        SystemFacade facade = game.getSystemFacade();

        facade.getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav" , facade.getAssetManager());
        game.changeController(new LoginController(game , authService));
    }

    public void exitGame() {
        System.out.println("[CMD] Exit Selezionato -> Transizione a ExitState");
        SystemFacade facade = game.getSystemFacade();

        facade.getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav" , facade.getAssetManager());
        // Cambio stato per chiusura sicura (Pattern State)
        game.changeAppState(new ExitState());
    }
}
