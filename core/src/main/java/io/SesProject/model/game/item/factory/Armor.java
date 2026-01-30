package io.SesProject.model.game.item.factory;

import io.SesProject.model.game.item.ItemType;

/*CONCRETE PRODUCT FACTORY METHOD PATTERN*/
public class Armor extends Item {
    public Armor(String name, int hpBonus) {
        super(name, "Aumenta la salute massima", ItemType.ARMOR, hpBonus);
    }

    @Override
    public String getStatDescription() {
        return "MAX HP +" + value;
    }
}
