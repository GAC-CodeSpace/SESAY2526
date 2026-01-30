package io.SesProject.model.game.item.factory;

import io.SesProject.model.game.item.ItemType;
import io.SesProject.model.memento.ItemMemento;

import static io.SesProject.model.game.item.ItemType.*;

/*ABSTRACT FACTORY, FACTORY METHOD PATTERN*/
public abstract class ItemFactory {
    public abstract Item createItem();

    public static Item createFromMemento(ItemMemento m) {
        if (m == null) return null;

        ItemType type = ItemType.valueOf(m.type); // String to Enum

        switch (type) {
            case WEAPON:
                return new Weapon(m.name, m.value);
            case ARMOR:
                return new Armor(m.name, m.value);
            case SKILL:
                return new SkillItem(m.name, m.value);
            default:
                return null;
        }
    }
}
