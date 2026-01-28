package io.SesProject.model.game.npc.factory;

import io.SesProject.RpgGame;
import io.SesProject.model.game.npc.NpcData;

/*CONCRETE PRODUCT OF NPC FACTORY METHOD APPLIED TO THE NPC SECTION*/
public class FriendlyNpc extends NpcEntity{
    public FriendlyNpc(NpcData data) {
        super(data);
    }

    @Override
    public void interact(RpgGame game) {
        System.out.println("[DIALOGO] " + getName() + ": " + data.getDialogue());
        // Qui in futuro potrá essere aperta la DialogBoxView
    }
}

