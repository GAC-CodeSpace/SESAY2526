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
        return createVillager(x, y); // Default
    }

    /**
     * NUOVO METODO SPECIFICO: Crea sempre un Villico.
     */
    public NpcEntity createVillager(float x, float y) {
        // Usa la ricetta del Director per il Villico
        director.constructVillager(builder, x, y);

        NpcData data = builder.getResult();
        return new FriendlyNpc(data);
    }

    public NpcEntity createSolider(float x , float y){
        director.constructSolider(builder , x , y);
        return new FriendlyNpc(builder.getResult());
    }

    /**
     * NUOVO METODO SPECIFICO: Crea sempre un Mercante.
     */
    public NpcEntity createMerchant(float x, float y) {
        // Usa la ricetta del Director per il Mercante
        director.constructMerchant(builder, x, y);

        NpcData data = builder.getResult();
        return new FriendlyNpc(data);
    }
}
