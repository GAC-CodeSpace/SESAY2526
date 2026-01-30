package io.SesProject.model.game.item.factory;

/*CONCRETE FACTORY, FACTORY METHOD PATTERN*/
public class ArmorFactory extends ItemFactory {
    private String type;
    public ArmorFactory(String type) { this.type = type; }

    @Override
    public Item createItem() {
        if (type.equals("Plate")) return new Armor("Corazza a Piastre", 100);
        return new Armor("Veste Logora", 10);
    }
}
