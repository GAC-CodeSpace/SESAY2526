package io.SesProject.controller;


import com.badlogic.gdx.Gdx;
import io.SesProject.RpgGame;
import io.SesProject.controller.command.ExitGameCommand;
import io.SesProject.controller.command.LoadGameCommand;
import io.SesProject.controller.command.NewGameCommand;
import io.SesProject.controller.command.OpenSettingsCommand;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.model.menu.MenuComposite;
import io.SesProject.model.menu.MenuItem;
import io.SesProject.service.AuthService;
import io.SesProject.service.SaveService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.MainMenuScreen;

public class MainMenuController extends BaseController {

    private SaveService saveService;

    public MainMenuController(RpgGame game, AuthService authService) {
        super(game, authService);
        this.saveService = game.getSaveService();
    }

    @Override
    protected BaseMenuScreen createView() {
        // Factory Method: Passiamo l'albero costruito alla View
        return new MainMenuScreen(this, buildMenuTree());
    }

    /**
     * Costruisce la struttura Composite del menu come da diagramma.
     */
    private MenuComponent buildMenuTree() {
        MenuComposite root = new MenuComposite("MAIN MENU");

        // 1. New Game (Crea una nuova partita)
        root.add(new MenuItem("NEW GAME", new NewGameCommand(this)));

        // 2. Load Game (Carica partita esistente)
        // Nota: Abilitiamo questo tasto solo se c'è effettivamente un salvataggio
        // o lo lasciamo sempre attivo e gestiamo la logica nel metodo.
        root.add(new MenuItem("LOAD GAME", new LoadGameCommand(this)));

        // 3. Settings (Impostazioni)
        root.add(new MenuItem("SETTINGS", new OpenSettingsCommand(this)));

        // 4. Exit (Esci)
        root.add(new MenuItem("EXIT", new ExitGameCommand(this)));

        return root;
    }

    // --- RECEIVER METHODS (Logica richiesta dai Command) ---

    public void newGame() {
        System.out.println("Azione: NEW GAME selezionata.");

        // Logica: Resettiamo i dati dell'utente corrente ai valori iniziali
        // (Utile se l'utente vuole ricominciare da zero con lo stesso account)
        // Oppure semplicemente avviamo la scena di gioco.

        // Esempio: Reset dati (opzionale)
        // game.getCurrentUser().resetProgress();

        startGameSession();
    }

    public void loadGame() {
        System.out.println("Azione: LOAD GAME selezionata.");

        // Logica: Poiché abbiamo già caricato il Memento al Login,
        // qui potremmo semplicemente confermare e avviare.
        // In un sistema a slot multipli, qui apriremmo un sotto-menu "Scegli Slot".

        startGameSession();
    }

    public void goToSettings() {
        System.out.println("Azione: SETTINGS selezionata.");
        // Cambio controller verso le impostazioni
        game.changeController(new SettingsController(game, authService));
    }

    public void exitGame() {
        System.out.println("Azione: EXIT selezionata.");
        Gdx.app.exit();
    }

    // Helper privato per avviare il gioco
    private void startGameSession() {
        System.out.println("Avvio della GameScreen...");
        // Qui passerai al GameController vero e proprio
        // game.changeController(new GameController(game, authService));
    }
}
