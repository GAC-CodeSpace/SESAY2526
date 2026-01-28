package io.SesProject.model.game.npc.factory;


import io.SesProject.model.game.npc.EnemyTemplate;
import io.SesProject.model.game.npc.NpcData;
import io.SesProject.model.game.npc.builder.NpcBuilder;
import io.SesProject.model.game.npc.builder.NpcDirector;
import io.SesProject.model.game.npc.builder.StandardNpcBuilder;
import io.SesProject.service.npc.Bestiary;

public class DungeonEnemyFactory extends NpcFactory {

    private NpcDirector director = new NpcDirector();
    private NpcBuilder builder = new StandardNpcBuilder();
    private int levelId; // Il livello del dungeon (1, 2, 3...)

    public DungeonEnemyFactory(int levelId) {
        this.levelId = levelId;
    }

    @Override
    public NpcEntity createNpc(float x, float y) {
        // 1. Chiede un minion adatto al livello corrente
        EnemyTemplate template = Bestiary.getMinionForLevel(levelId);

        // 2. Costruisce
        director.constructEnemyFromTemplate(builder, x, y, template);
        NpcData data = builder.getResult();

        return new HostileNpc(data);
    }
}
