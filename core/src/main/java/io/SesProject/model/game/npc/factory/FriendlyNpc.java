package io.SesProject.model.game.npc.factory;

import io.SesProject.RpgGame;
import io.SesProject.model.game.npc.NpcData;
import io.SesProject.view.game.GameScreen;

/*CONCRETE PRODUCT OF NPC FACTORY METHOD APPLIED TO THE NPC SECTION*/
public class FriendlyNpc extends NpcEntity{
    public FriendlyNpc(NpcData data) {
        super(data);
    }

    @Override
    public void interact(RpgGame game) {
        System.out.println("[INTERACTION] Parlo con: " + getName());

        // Recuperiamo la schermata corrente (che sappiamo essere GameScreen)
        // Questo è un accoppiamento accettabile nel contesto LibGDX game loop
        if (game.getScreen() instanceof GameScreen) {
            GameScreen screen =
                (GameScreen) game.getScreen();

            // Mostra dialogo Amichevole (isHostile = false)
            screen.showNpcDialog(this.data, false);
        }
    }
}

