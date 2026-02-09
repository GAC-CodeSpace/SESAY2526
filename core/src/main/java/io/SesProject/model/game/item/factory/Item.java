package io.SesProject.model.game.item.factory;

import io.SesProject.model.game.item.ItemType;
import io.SesProject.model.memento.ItemMemento;

/*ABSTRACT PRODUCT FACTORY METHOD PATTERN*/
public abstract class Item {
    protected String name;
    protected String description;
    protected ItemType type;
    protected int value; // Rappresenta il danno (Weapon) o HP bonus (Armor)

    public Item(String name, String description, ItemType type, int value) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
    }

    // --- MEMENTO: SAVE ---
    public ItemMemento save() {
        return new ItemMemento(
            this.name,
            this.description,
            this.type.name(), // Enum to String
            this.value
        );
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemType getType() { return type; }
    public int getValue() { return value; }

    // Metodo per descrizione breve nella UI
    public abstract String getStatDescription();

    public void setValue(int newValue) {
        this.value = newValue;
    }
}
