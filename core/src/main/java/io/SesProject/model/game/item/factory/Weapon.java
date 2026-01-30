package io.SesProject.model.game.item.factory;

import io.SesProject.model.game.item.ItemType;

/*CONCRETE PRODUCT FACTORY METHOD PATTERN*/
public class Weapon extends Item {
    public Weapon(String name, int damageBonus) {
        super(name, "Aumenta il danno base", ItemType.WEAPON, damageBonus);
    }

    @Override
    public String getStatDescription() {
        return "ATK +" + value;
    }
}
