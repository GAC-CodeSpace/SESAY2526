package io.sesProject.model.game.item;

import io.SesProject.model.game.item.ItemType;
import io.SesProject.model.game.item.factory.*;
import io.SesProject.model.memento.ItemMemento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testWeaponCreationAndStats() {
        Weapon weapon = new Weapon("Sword", 10);
        assertEquals("Sword", weapon.getName());
        assertEquals(10, weapon.getValue());
        assertEquals(ItemType.WEAPON, weapon.getType());
        assertNotNull(weapon.getStatDescription());
        assertTrue(weapon.getStatDescription().contains("ATK +10"));
    }

    @Test
    void testArmorCreationAndStats() {
        Armor armor = new Armor("Shield", 5);
        assertEquals("Shield", armor.getName());
        assertEquals(5, armor.getValue());
        assertEquals(ItemType.ARMOR, armor.getType());
        assertTrue(armor.getStatDescription().contains("MAX HP +5"));
    }

    @Test
    void testSkillItemCreationAndStats() {
        SkillItem skill = new SkillItem("Fireball", 20);
        assertEquals("Fireball", skill.getName());
        assertEquals(20, skill.getValue());
        assertEquals(ItemType.SKILL, skill.getType());
        assertTrue(skill.getStatDescription().contains("PWR 20"));
    }

    @Test
    void testFactoryRestore() {
        Weapon original = new Weapon("Axe", 15);
        ItemMemento memento = original.save();

        Item restored = ItemFactory.createFromMemento(memento);

        assertNotNull(restored);
        assertTrue(restored instanceof Weapon);
        assertEquals("Axe", restored.getName());
        assertEquals(15, restored.getValue());
    }

    @Test
    void testFactoryRestoreDetailed() {
        // Test different types
        Item armor = new Armor("Plate", 50);
        Item restoredArmor = ItemFactory.createFromMemento(armor.save());
        assertTrue(restoredArmor instanceof Armor);
        assertEquals(50, restoredArmor.getValue());

        Item skill = new SkillItem("Heal", 30);
        Item restoredSkill = ItemFactory.createFromMemento(skill.save());
        assertTrue(restoredSkill instanceof SkillItem);
        assertEquals(30, restoredSkill.getValue());
    }
}
