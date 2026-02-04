package io.SesProject.model.game.npc.factory;

import io.SesProject.RpgGame;
import io.SesProject.model.game.GameObject;
import io.SesProject.model.game.PlayerEntity;
import io.SesProject.model.game.npc.NpcData;

/*ABSTRACT PRODUCT OF NPC FACTORY METHOD APPLIED TO THE NPC SECTION*/
public abstract class NpcEntity extends GameObject {
    protected NpcData data;

    public NpcEntity(NpcData data) {
        super();
        this.data = data;
        this.x = data.getX();
        this.y = data.getY();
        this.width = 32;
        this.height = 32;
        this.setMovementStrategy(data.getMovementStrategy());
    }

    public abstract void interact(RpgGame game, PlayerEntity interactor);

    public String getName() { return data.getName(); }
    public boolean isHostile() { return data.isHostile(); }
    public int getMaxHp() { return data.getMaxHp(); } // Utile per il combat

    public boolean isDefeated() {
        return data.isDefeated();
    }

    public NpcData getData() {
        return this.data;
    }

    @Override
    public String getSpriteName() {
        // Restituisce il nome dello sprite impostato dal Builder
        return data.getSpriteName();
    }
}
