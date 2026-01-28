package io.SesProject.model.game.npc.factory;


import io.SesProject.model.game.npc.EnemyTemplate;
import io.SesProject.model.game.npc.NpcData;
import io.SesProject.model.game.npc.builder.NpcBuilder;
import io.SesProject.model.game.npc.builder.NpcDirector;
import io.SesProject.model.game.npc.builder.StandardNpcBuilder;
import io.SesProject.service.npc.Bestiary;

/**
 * Concrete Creator specifico per i Boss.
 */

public class BossFactory extends NpcFactory {

    private NpcDirector director = new NpcDirector();
    private NpcBuilder builder = new StandardNpcBuilder();
    private int levelId;

    public BossFactory(int levelId) {
        this.levelId = levelId;
    }

    @Override
    public NpcEntity createNpc(float x, float y) {
        // 1. Chiede il BOSS specifico di questo livello
        EnemyTemplate template = Bestiary.getBossForLevel(levelId);

        // 2. Costruisce usando la ricetta del Boss
        director.constructBossFromTemplate(builder, x, y, template);

        NpcData data = builder.getResult();

        return new HostileNpc(data);
    }
}
