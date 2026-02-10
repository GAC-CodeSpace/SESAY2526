package io.SesProject.model.game.combat;

import io.SesProject.model.game.npc.NpcData;

public class EnemyCombatant extends Combatant {

    private String spriteName;

    // Costruttore Esistente (per test o fallback)
    public EnemyCombatant(String name, int hp) {
        super(name, hp);
    }

    public EnemyCombatant(NpcData data) {
        super(data.getName(), data.getMaxHp());
        this.spriteName = data.getSpriteName();
        this.attackPower = data.getAttackPower();
        this.spriteName = data.getSpriteName();
    }

    @Override
    public String getSpriteName() {
        return this.spriteName;
    }
}
