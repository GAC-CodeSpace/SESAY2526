package io.SesProject.model.game.item.factory;


/*CONCRETE FACTORY, FACTORY METHOD PATTERN*/
public class WeaponFactory extends ItemFactory {
    private String type;
    public WeaponFactory(String type) { this.type = type; }

    @Override
    public Item createItem() {
        if (type.equals("Sword")) return new Weapon("Spada di Ferro", 10);
        return new Weapon("Bastone", 5);
    }
}
