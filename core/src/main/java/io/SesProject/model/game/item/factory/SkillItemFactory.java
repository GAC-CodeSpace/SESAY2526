package io.SesProject.model.game.item.factory;


public class SkillItemFactory extends ItemFactory {
    private String name;
    public SkillItemFactory(String name) { this.name = name; }

    @Override
    public Item createItem() {
        return new SkillItem(name, 20); // Valore potenza default
    }
}
