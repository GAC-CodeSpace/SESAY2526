package io.SesProject.model.game.npc.builder;

import io.SesProject.model.game.movementStrategy.MovementStrategy;
import io.SesProject.model.game.npc.NpcData;

/*ABSTRACT BUILDER*/
public interface NpcBuilder {
    void reset();
    void buildIdentity(String name, String sprite);
    void buildPosition(float x, float y);
    void buildCombatStats(boolean hostile, int hp , int attackPower);
    void buildInteraction(String dialogue);
    void buildBehavior(MovementStrategy behavior);
    void buildRewards(int xpReward, int karmaReward);

    NpcData getResult();
}
