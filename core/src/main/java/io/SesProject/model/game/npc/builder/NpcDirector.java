package io.SesProject.model.game.npc.builder;


import io.SesProject.model.game.movementStrategy.InputMovementStrategy;
import io.SesProject.model.game.movementStrategy.StaticStrategy;
import io.SesProject.model.game.npc.EnemyTemplate;

/*DIRECTOR OF BUILDER PATTERN*/
public class NpcDirector {
    // --NPC AMICHEVOLE--
    public void constructVillager(NpcBuilder builder, float x, float y, String name) {
        builder.reset();
        builder.buildIdentity(name, "villager_sprite");
        builder.buildPosition(x, y);
        builder.buildCombatStats(false, 10);
        builder.buildInteraction("Salve viaggiatore! Benvenuto al villaggio.");
        builder.buildBehavior(new StaticStrategy());
    }

    // Metodo generico per la generico per la generazione di nemici generici basati sulla classe template
    public void constructEnemyFromTemplate(NpcBuilder builder, float x, float y, EnemyTemplate template) {
        builder.reset();

        // Usa i dati del template
        builder.buildIdentity(template.name, template.spriteName);
        builder.buildCombatStats(true, template.maxHp);

        // Usa i dati di posizione passati
        builder.buildPosition(x, y);

        // Configurazioni standard per nemici
        builder.buildInteraction(null); // Non parlano

        // Strategia di movimento (potrebbe essere passata nel template in futuro)
        builder.buildBehavior(new InputMovementStrategy());
    }

    // Costruzione BOSS (Da Template)
    public void constructBossFromTemplate(NpcBuilder builder, float x, float y, EnemyTemplate template) {
        builder.reset();
        builder.buildIdentity(template.name, template.spriteName);

        // I boss sono ostili e usano gli HP del template
        builder.buildCombatStats(true, template.maxHp);
        builder.buildPosition(x, y);

        // Aggiungiamo un dialogo generico di sfida (o potremmo metterlo nel template)
        builder.buildInteraction("Sono il " + template.name + "! Preparati a perire!");

        // I Boss stanno fermi ad aspettare il giocatore (StaticStrategy)
        builder.buildBehavior(new StaticStrategy());
    }
}
