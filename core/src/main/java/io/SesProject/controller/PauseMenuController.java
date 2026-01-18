package io.SesProject.controller;


import io.SesProject.RpgGame;
import io.SesProject.controller.command.OpenSettingsCommand;
import io.SesProject.controller.command.QuitToMainCommand;
import io.SesProject.controller.command.ResumeGameCommand;
import io.SesProject.controller.command.SaveGameCommand;
import io.SesProject.controller.enumsContainer.MenuSource;
import io.SesProject.controller.state.PlayState;
import io.SesProject.model.GameSession;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.model.menu.MenuComposite;
import io.SesProject.model.menu.MenuItem;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.PauseMenuScreen;
import io.SesProject.model.memento.Memento;

public class PauseMenuController extends BaseController {

    public PauseMenuController(RpgGame game, AuthService authService) {
        super(game, authService);
        // Nota: Non fermiamo la musica qui, la lasciamo in background o la abbassiamo
    }

    @Override
    protected BaseMenuScreen createView() {
        return new PauseMenuScreen(this, buildMenuTree());
    }

    private MenuComponent buildMenuTree() {
        MenuComposite root = new MenuComposite("PAUSA DI GIOCO");

        root.add(new MenuItem("RIPRENDI", new ResumeGameCommand(this)));
        root.add(new MenuItem("SALVA PARTITA", new SaveGameCommand(this)));
        root.add(new MenuItem("IMPOSTAZIONI" , new OpenSettingsCommand(this)));
        root.add(new MenuItem("ESCI AL MENU", new QuitToMainCommand(this)));

        return root;
    }

    // --- RECEIVER METHODS ---

    public void resumeGame() {
        System.out.println("[CMD] Riprendi Gioco");
        // Torna al GameController. Dato che la sessione è in game.currentSession,
        // il GameController ripartirà da dove eravamo.
        game.changeAppState(new PlayState());
    }

    public void saveGame() {
        System.out.println("[CMD] Salvataggio Manuale...");

        GameSession session = game.getCurrentSession();
        String username = game.getCurrentUser().getUsername();


        int slotId = 1;

        // 2. Creiamo il Memento
        Memento snapshot = session.save();

        // 3. Salviamo sovrascrivendo
        game.getSystemFacade().getSaveService().saveGame(snapshot, username, slotId);

        if (view instanceof PauseMenuScreen) {
            ((PauseMenuScreen)view).showMessage("SALVATAGGIO", "Partita salvata con successo!");
        }
    }

    public void quitToMain() {
        System.out.println("[CMD] Uscita al Menu Principale");

        // Chiudiamo la sessione attiva
        game.setCurrentSession(null);

        // Cambiamo controller
        game.changeController(new MainMenuController(game, authService));
    }

    public void goToSettings() {
        System.out.println("[CMD] Settings Selezionato (da Pausa)");
        // Passiamo la sorgente PAUSE_MENU
        game.changeController(new SettingsController(game, authService, MenuSource.PAUSE_MENU));
    }
}
