package io.SesProject.model.game.item.factory;

import io.SesProject.model.game.item.ItemType;

/*CONCRETE PRODUCT FACTORY METHOD PATTERN*/
public class SkillItem extends Item {

    // In futuro dovrá essere inserito il riferimento al Behavior o l'ID della skill di combattimento
    // private String skillId;
    // private SkillBehavior behavior;

    public SkillItem(String name, int powerLevel) {
        super(name, "Abilità equipaggiabile", ItemType.SKILL, powerLevel);
    }

    @Override
    public String getStatDescription() {
        return "PWR " + value; // O "Cooldown: " + value
    }
}
