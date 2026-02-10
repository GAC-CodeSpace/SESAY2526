package io.SesProject.controller;


import io.SesProject.RpgGame;
import io.SesProject.controller.command.Command;
import io.SesProject.controller.command.menuCommand.*;
import io.SesProject.controller.enumsContainer.MenuSource;
import io.SesProject.controller.state.PlayState;
import io.SesProject.model.GameSession;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.model.menu.MenuComposite;
import io.SesProject.model.menu.MenuItem;
import io.SesProject.service.AuthService;
import io.SesProject.service.SystemFacade;
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
        root.add(new MenuItem("INVENTARIO" , new OpenInventoryCommand(this)));
        root.add(new MenuItem("IMPOSTAZIONI" , new OpenSettingsCommand(this)));
        root.add(new MenuItem("ESCI AL MENU", new QuitToMainCommand(this)));

        return root;
    }

    // --- RECEIVER METHODS ---

    public void resumeGame() {
        System.out.println("[CMD] Riprendi Gioco");
        // Torna al GameController. Dato che la sessione è in game.currentSession,
        // il GameController ripartirà da dove eravamo.
        SystemFacade facade = game.getSystemFacade();

        facade.getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav" , facade.getAssetManager());
        game.changeAppState(new PlayState());
    }

    public void saveGame() {
        System.out.println("[CMD] Salvataggio Manuale...");

        GameSession session = game.getCurrentSession();
        String username = game.getCurrentUser().getUsername();

        // Recupera il GameController attivo per ottenere lo stato del mondo
        GameController gameController = game.getActiveGameController();

        if (session == null || gameController == null) {
            System.err.println("[ERROR] Impossibile salvare: sessione o gioco non attivi.");
            if (view instanceof PauseMenuScreen) {
                ((PauseMenuScreen)view).showMessage("ERRORE", "Nessuna partita da salvare.");
            }
            return;
        }

        // --- CORREZIONE FONDAMENTALE ---
        // 1. SINCRONIZZA: Aggiorna la GameSession con lo stato vivo del GameController
        session.updateNpcsFromWorld(gameController.getWorldEntities());

        // Ora session.getWorldNpcs() contiene solo i nemici rimasti vivi.

        // 2. CREA MEMENTO: Lo snapshot ora conterrà la lista aggiornata
        Memento snapshot = session.save();

        // 3. SALVA SU DISCO
        // Recuperiamo lo slot ID della sessione
        int slotId = session.getSaveSlotId(); // Devi aggiungere questo campo a GameSession!

        game.getSystemFacade().getSaveService().saveGame(snapshot, username, slotId);

        // Feedback
        if (view instanceof PauseMenuScreen) {
            ((PauseMenuScreen)view).showMessage("SALVATAGGIO", "Partita salvata nello Slot " + slotId);
        }
    }

    public void quitToMain() {
        System.out.println("[CMD] Uscita al Menu Principale");

        // 1. Chiudi sessione
        game.setCurrentSession(null);

        // 2. CORREZIONE: Distruggi il controller attivo
        game.setActiveGameController(null);
        SystemFacade facade = game.getSystemFacade();

        facade.getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav" , facade.getAssetManager());
        // 3. Torna al menu
        game.changeController(new MainMenuController(game, authService));
    }

    public void goToSettings() {
        System.out.println("[CMD] Settings Selezionato (da Pausa)");
        SystemFacade facade = game.getSystemFacade();

        facade.getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav" , facade.getAssetManager());
        // Passiamo la sorgente PAUSE_MENU
        game.changeController(new SettingsController(game, authService, MenuSource.PAUSE_MENU));
    }

    public void openInventory() {
        SystemFacade facade = game.getSystemFacade();

        facade.getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav" , facade.getAssetManager());
        game.changeController(new InventoryController(game, authService));
    }
}
