package io.SesProject.controller;



import io.SesProject.RpgGame;
import io.SesProject.model.GameSession;
import io.SesProject.model.SaveMetadata;
import io.SesProject.model.memento.Memento;
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
     * AZIONE: Click su un bottone di salvataggio.
     * Carica il Memento, ripristina la Sessione e avvia il Gioco.
     */
    public void loadSlot(int slotId) {
        System.out.println("[CMD] Caricamento Slot " + slotId + "...");

        // 1. Recupera il nome utente dal contesto (Model Identity)
        String username = game.getCurrentUser().getUsername();

        // 2. CARETAKER: Chiede al Service di caricare il Memento dal disco
        // Usa la Facade per accedere al servizio
        Memento memento = game.getSystemFacade().getSaveService().loadGame(username, slotId);

        if (memento != null) {
            System.out.println("[SYSTEM] Memento caricato. Ripristino stato...");

            // 3. ORIGINATOR: Crea una sessione vuota
            GameSession session = new GameSession();

            // 4. RESTORE: Idrata la sessione con i dati del Memento
            session.restore(memento);

            // 5. Aggiorna il contesto globale del gioco
            game.setCurrentSession(session);

            // 6. Transizione al GameController (Schermata Nera)
            System.out.println("[SYSTEM] Avvio GameController...");
            game.changeController(new GameController(game, authService));

        } else {
            System.err.println("[ERROR] Impossibile caricare il file (Memento nullo).");
            // Qui potresti mostrare un popup di errore sulla view
            if (view instanceof io.SesProject.view.BaseMenuScreen) {
                ((io.SesProject.view.BaseMenuScreen)view).showMessage("ERRORE", "File di salvataggio corrotto o mancante.");
            }
        }
    }

    public void backToMain() {
        game.changeController(new MainMenuController(game, authService));
    }
}
