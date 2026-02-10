package io.SesProject.controller;


import io.SesProject.RpgGame;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.GameOverScreen;

public class GameOverController extends BaseController {

    public GameOverController(RpgGame game, AuthService authService) {
        super(game, authService);
    }

    @Override
    protected BaseMenuScreen createView() {
        return new GameOverScreen(this);
    }

    // --- AZIONI (Receiver) ---

    public void retryLastSave() {
        System.out.println("[CMD] Riprova dall'ultimo salvataggio...");
        // Qui dovremmo idealmente ricaricare l'ultimo slot usato.
        // Se l'oggetto User tiene traccia dell'ultimo slot caricato (user.getLastLoadedSlotId()), usiamo quello.
        // Altrimenti, per semplicità, mandiamo l'utente alla schermata di caricamento.

        // Opzione A: Vai alla lista caricamenti (più sicuro)
        game.changeController(new LoadGameController(game, authService));

        // Opzione B (Migliore): Ricarica diretta (Richiede che RPGGame sappia quale slot era attivo)
        // int slot = game.getCurrentSessionSlot();
        // game.getSystemFacade().getSaveService().loadGame(username, slot); ...
    }

    public void returnToMainMenu() {
        System.out.println("[CMD] Ritorno al Menu Principale...");

        // Pulizia sessione corrente
        game.setCurrentSession(null);
        game.setActiveGameController(null); // Distruggi controller di gioco

        // Cambio Controller
        game.changeController(new MainMenuController(game, authService));
    }
}
