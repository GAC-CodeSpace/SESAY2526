package io.SesProject.model.game.npc.builder;

import io.SesProject.model.game.movementStrategy.MovementStrategy;
import io.SesProject.model.game.npc.NpcData;

public class StandardNpcBuilder implements NpcBuilder{
    private NpcData npc;

    public StandardNpcBuilder() {
        this.reset();
    }

    @Override
    public void reset() {
        this.npc = new NpcData();
    }

    @Override
    public void buildIdentity(String name, String sprite) {
        npc.setName(name);
        npc.setSpriteName(sprite);
    }

    @Override
    public void buildPosition(float x, float y) {
        npc.setPosition(x, y);
    }

    @Override
    public void buildCombatStats(boolean hostile, int hp) {
        npc.setHostile(hostile);
        npc.setMaxHp(hp);
    }

    @Override
    public void buildInteraction(String dialogue) {
        npc.setDialogue(dialogue);
    }

    @Override
    public void buildBehavior(MovementStrategy behavior) {
        npc.setMovementStrategy(behavior);
    }

    @Override
    public NpcData getResult() {
        return this.npc;
    }
}
