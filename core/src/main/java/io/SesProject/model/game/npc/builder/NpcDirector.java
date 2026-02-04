package io.SesProject.model.game.npc.builder;


import io.SesProject.model.game.movementStrategy.InputMovementStrategy;
import io.SesProject.model.game.movementStrategy.StaticStrategy;
import io.SesProject.model.game.npc.EnemyTemplate;

/*DIRECTOR OF BUILDER PATTERN*/

public class NpcDirector {

    // --- NPC AMICHEVOLI ---

    // Ricetta 1: Villico Comune
    public void constructVillager(NpcBuilder builder, float x, float y) {
        builder.reset();
        builder.buildIdentity("Villico", "villager"); // Sprite generico
        builder.buildPosition(x, y);
        builder.buildCombatStats(false, 10 , 0);
        builder.buildInteraction("Liberaci dai mostri, ti prego!!! sapro' ricompensarti");
        builder.buildBehavior(new StaticStrategy());
    }

    // Ricetta 2: Mercante (NUOVO)
    public void constructMerchant(NpcBuilder builder, float x, float y) {
        builder.reset();
        builder.buildIdentity("Mercante", "merchant"); // Sprite specifico
        builder.buildPosition(x, y);
        builder.buildCombatStats(false, 100 , 0); // I mercanti sono robusti!
        builder.buildInteraction("Il carro con le mie scorte e' stato rubato dai mostri, trovalo ti prego!!! sapro' ricompensarti");
        builder.buildBehavior(new StaticStrategy());
    }

    public void constructSolider(NpcBuilder builder, float x, float y) {
        builder.reset();
        builder.buildIdentity("soldato", "solider"); // Sprite specifico
        builder.buildPosition(x, y);
        builder.buildCombatStats(false, 100 , 0); // I mercanti sono robusti!
        builder.buildInteraction("C'ero quasi..., sono ferito aiutami!!! sapro' ricompensarti");
        builder.buildBehavior(new StaticStrategy());
    }

    // --- NEMICI (Generic Template Based) ---

    // Usato sia per Scheletri (Minion) che per Scheletro Gigante (Boss)
    // poiché passiamo il template dal Bestiario
    public void constructEnemyFromTemplate(NpcBuilder builder, float x, float y, EnemyTemplate template) {
        builder.reset();
        builder.buildIdentity(template.name, template.spriteName);
        builder.buildCombatStats(true, template.maxHp , template.attackPower);
        builder.buildPosition(x, y);

        // Distinzione dialogo Boss vs Minion (basata sul nome o HP)
        if (template.name.contains("Gigante")) {
            builder.buildInteraction("CRAAASH! (Rumore di ossa giganti che si muovono)");
            builder.buildBehavior(new StaticStrategy()); // Boss fermo
        } else {
            builder.buildInteraction("Ti annienteremo!!!"); // Minion muti
            builder.buildBehavior(new InputMovementStrategy()); // Minion si muovono
        }
    }
}
