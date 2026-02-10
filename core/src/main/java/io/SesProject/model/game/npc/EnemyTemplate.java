package io.SesProject.model.game.npc;


/**
 * DTO che contiene la configurazione statica per un tipo di nemico.
 * Usato per passare i parametri dal Bestiario al Director.
 */
public class EnemyTemplate {
    public String name;
    public String spriteName;
    public int maxHp;
    public int attackPower;
    public int xpReward;
    public int karmaReward;

    public EnemyTemplate(String name, String spriteName, int maxHp, int attackPower, int xpReward, int karmaReward) {
        this.name = name;
        this.spriteName = spriteName;
        this.maxHp = maxHp;
        this.attackPower = attackPower;
        this.xpReward = xpReward;
        this.karmaReward = karmaReward;
    }
}
