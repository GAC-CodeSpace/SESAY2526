package io.SesProject.model.game.npc.factory;

import io.SesProject.RpgGame;
import io.SesProject.controller.GameController;
import io.SesProject.model.game.PlayerEntity;
import io.SesProject.model.game.npc.NpcData;
import io.SesProject.view.game.GameScreen;

/*CONCRETE PRODUCT OF NPC FACTORY METHOD APPLIED TO THE NPC SECTION*/

public class FriendlyNpc extends NpcEntity {

    public FriendlyNpc(NpcData data) {
        super(data);
    }

    @Override
    public void interact(RpgGame game, PlayerEntity interactor) {
        // 1. Controllo Karma del giocatore specifico
        int playerKarma = interactor.getData().getKarma();
        System.out.println("[DEBUG] Karma attuale di " + interactor.getName() + ": " + playerKarma);

        // Otteniamo il riferimento alla schermata e al controller
        GameScreen screen = null;
        GameController gc = null;

        if (game.getScreen() instanceof GameScreen) {
            screen = (GameScreen) game.getScreen();
            gc = screen.getController();
        }

        if (playerKarma > 0) {
            // --- KARMA POSITIVO ---
            System.out.println("[DIALOGO] " + getName() + " parla con " + interactor.getName());

            if (gc != null && screen != null) {
                // Impostiamo chi sta parlando per gestire le ricompense successive
                gc.setPlayerInDialog(interactor.getData());

                // Avvia il dialogo (questo gestirà endDialogState alla chiusura della finestra)
                screen.showNpcDialog(this.data, false);
            }

        } else {
            // --- KARMA NEGATIVO/ZERO ---
            System.out.println("[DIALOGO] " + getName() + " ignora " + interactor.getName());

            if (screen != null) {
                screen.showMessage("Rifiuto", getName() + " ti guarda con disprezzo e non parla.");
            }

            // [FIX CRITICO]
            // Dobbiamo sbloccare manualmente il controller perché qui non si apre
            // un dialogo interattivo che lo fa da solo.
            if (gc != null) {
                System.out.println("[GAME] Chiusura forzata dialogo per Karma negativo.");
                gc.endDialogState();
            }
        }
    }
}


