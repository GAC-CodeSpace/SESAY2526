package io.SesProject.model.game.combat;

import io.SesProject.model.game.npc.NpcData;

public class EnemyCombatant extends Combatant {

    private String spriteName;
    private NpcData sourceData; // Keep reference to get display name

    // Costruttore Esistente (per test o fallback)
    public EnemyCombatant(String name, int hp) {
        super(name, hp);
    }

    public EnemyCombatant(NpcData data) {
        super(data.getName(), data.getMaxHp());
        this.sourceData = data; // Store reference
        this.spriteName = data.getSpriteName();
        this.attackPower = data.getAttackPower();
        this.spriteName = data.getSpriteName();
    }

    @Override
    public String getDisplayName() {
        // Return display name from source data, fallback to name if not available
        return sourceData != null ? sourceData.getDisplayName() : name;
    }

    @Override
    public String getSpriteName() {
        return this.spriteName;
    }
}

