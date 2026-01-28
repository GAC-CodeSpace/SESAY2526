package io.SesProject.model.game.combat;

import io.SesProject.model.game.npc.NpcData;

public class EnemyCombatant extends Combatant {

    // Costruttore Esistente (per test o fallback)
    public EnemyCombatant(String name, int hp) {
        super(name, hp);
    }

    public EnemyCombatant(NpcData data) {
        super(data.getName(), data.getMaxHp());
        // Qui in futuro potrai passare anche statistiche di attacco/difesa se aggiunte a NPCData
    }
}
