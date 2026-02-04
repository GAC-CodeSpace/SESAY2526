package io.SesProject.model.game.npc.factory;

import io.SesProject.RpgGame;
import io.SesProject.controller.state.CombatState;
import io.SesProject.model.game.PlayerEntity;
import io.SesProject.model.game.npc.NpcData;
import io.SesProject.view.game.GameScreen;

/*CONCRETE PRODUCT OF NPC FACTORY METHOD APPLIED TO THE NPC SECTION*/
public class HostileNpc extends NpcEntity{

    public HostileNpc(NpcData data) {
        super(data);
    }

    /**
     * Metodo chiamato quando c'è una collisione o interazione.
     * Richiede il riferimento al gioco per cambiare stato.
     */
    @Override
    public void interact(RpgGame game , PlayerEntity interactor) {
        System.out.println("[INTERACTION] Incontro con: " + getName());

        if (game.getScreen() instanceof GameScreen) {
            GameScreen screen =
                (GameScreen) game.getScreen();

            // Mostra dialogo Ostile (isHostile = true)
            // L'utente dovrà cliccare "COMBATTI" per avviare la battaglia reale
            screen.showNpcDialog(this.data, true);
        }
    }
}


