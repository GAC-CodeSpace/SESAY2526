package io.SesProject.controller;



import io.SesProject.RpgGame;
import io.SesProject.model.SaveMetadata;
import io.SesProject.service.AuthService;
import io.SesProject.service.SaveService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.LoadGameScreen;

import java.util.List;

public class LoadGameController extends BaseController {

    private SaveService saveService;

    public LoadGameController(RpgGame game, AuthService authService) {
        super(game, authService);
        this.saveService = game.getSystemFacade().getSaveService();
    }

    @Override
    protected BaseMenuScreen createView() {
        return new LoadGameScreen(this);
    }

    /**
     * Fornisce alla View la lista dei salvataggi da mostrare.
     */
    public List<SaveMetadata> getSaveList() {
        String currentUser = game.getCurrentUser().getUsername();
        return saveService.getSaveSlots(currentUser);
    }

    /**
     * AZIONE: L'utente ha cliccato su uno slot specifico.
     * Carica la partita e avvia il gioco.

    public void loadSlot(int slotId) {
        System.out.println("[CMD] Caricamento Slot " + slotId + "...");
        String username = game.getCurrentUser().getUsername();

        // 1. Carica il Memento dal disco (Caretaker)
        Memento memento = saveService.loadGame(username, slotId);

        if (memento != null) {
            // 2. Crea una sessione vuota (Originator)
            GameSession session = new GameSession();

            // 3. Ripristina lo stato (Restore)
            session.restore(memento);

            // 4. Imposta la sessione nel contesto del gioco
            game.setCurrentSession(session);

            // 5. Avvia il gioco (Factory Method -> GameController)
            System.out.println("[SYSTEM] Partita avviata con successo!");
            game.changeController(new GameController(game, authService));
        } else {
            System.err.println("[ERROR] Impossibile caricare il salvataggio.");
        }
    }*/

    public void backToMain() {
        game.changeController(new MainMenuController(game, authService));
    }
}
