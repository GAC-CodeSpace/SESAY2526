package io.SesProject.model.game.npc.factory;

import io.SesProject.RpgGame;
import io.SesProject.controller.state.CombatState;
import io.SesProject.model.game.npc.NpcData;

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
    public void interact(RpgGame game) {
        System.out.println("[COMBAT] " + getName() + " ti ha ingaggiato!");

        // --- INTEGRAZIONE FINALE ---
        // Passiamo 'this.data' (i dati di QUESTO specifico Goblin/Scheletro)
        // al nuovo stato di combattimento.
        game.changeAppState(new CombatState(this.data));

        // Nota: Dopo il combattimento, questo NPC dovrà essere rimosso dalla mappa
        // (Logica da gestire nel GameController al ritorno dal CombatState)
    }
}


