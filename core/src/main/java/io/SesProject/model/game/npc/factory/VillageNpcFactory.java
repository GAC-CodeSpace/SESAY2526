package io.SesProject.model.game.npc.factory;

import io.SesProject.model.game.npc.NpcData;
import io.SesProject.model.game.npc.builder.NpcBuilder;
import io.SesProject.model.game.npc.builder.NpcDirector;
import io.SesProject.model.game.npc.builder.StandardNpcBuilder;

import java.util.Random;

/*CONCRETE CREATOR OF NPC FACTORY METHOD APPLIED TO THE NPC SECTION*/
public class VillageNpcFactory extends NpcFactory{
    private NpcDirector director = new NpcDirector();
    private NpcBuilder builder = new StandardNpcBuilder();
    private Random rand = new Random();

    @Override
    public NpcEntity createNpc(float x, float y) {
        String[] names = {"Oste", "Mercante", "Anziano"};
        String name = names[rand.nextInt(names.length)];

        // 1. Costruisci i dati complessi (Builder)
        director.constructVillager(builder, x, y, name);
        NpcData data = builder.getResult();

        // 2. Ritorna l'entità specifica (Factory Method)
        return new FriendlyNpc(data);
    }
}
