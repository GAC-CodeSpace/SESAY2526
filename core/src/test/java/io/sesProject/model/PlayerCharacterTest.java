package io.sesProject.model;

import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.item.ItemType;
import io.SesProject.model.game.item.factory.Item;
import io.SesProject.model.memento.PlayerCharacterMemento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerCharacterTest {

    private PlayerCharacter player;

    @BeforeEach
    void setUp() {
        player = new PlayerCharacter("Hero", "Warrior");
    }

    @Test
    void testInitialization() {
        assertEquals("Hero", player.getName());
        assertEquals("Warrior", player.getArchetype());
        assertEquals(1, player.getLevel());
        assertEquals(100, player.getMaxHp());
        assertEquals(100, player.getHp());
        assertEquals(0, player.getExperience());
        assertEquals(10, player.getKarma());
    }

    @Test
    void testAddExperienceAndLevelUp() {
        // Level 1 -> 2 requires 20 XP
        assertFalse(player.addExperience(10));
        assertEquals(10, player.getExperience());
        assertEquals(1, player.getLevel());

        // Add 10 more to reach 20 -> Level Up!
        assertTrue(player.addExperience(10));
        assertEquals(2, player.getLevel());
        assertEquals(0, player.getExperience()); // Consumed exact amount

        // Check HP increase on level up
        assertEquals(110, player.getMaxHp());

        // Level 2 -> 3 requires 40 XP
        assertTrue(player.addExperience(45)); // 5 extra
        assertEquals(3, player.getLevel());
        assertEquals(5, player.getExperience());
        assertEquals(120, player.getMaxHp());
    }

    @Test
    void testModifyKarma() {
        player.modifyKarma(5);
        assertEquals(15, player.getKarma());

        player.modifyKarma(-10);
        assertEquals(5, player.getKarma());
    }

    @Test
    void testEquipItem_Weapon() {
        // Weapon(String name, int damage)
        io.SesProject.model.game.item.factory.Weapon sword = new io.SesProject.model.game.item.factory.Weapon("Iron Sword", 10);

        player.addItem(sword);
        player.equipItem(sword);

        assertEquals(sword, player.getEquippedWeapon());
        assertFalse(player.getInventory().contains(sword));

        // Check stats (baseDamage 5 + weapon 10 = 15)
        assertEquals(15, player.getAttackPower());
    }

    @Test
    void testEquipItem_Armor() {
        // Armor(String name, int hpBonus)
        io.SesProject.model.game.item.factory.Armor armor = new io.SesProject.model.game.item.factory.Armor("Leather Armor", 20);

        player.addItem(armor);
        player.equipItem(armor);

        assertEquals(armor, player.getEquippedArmor());
        // Base MaxHP 100 + Armor 20 = 120
        assertEquals(120, player.getMaxHp());
    }

    @Test
    void testUnequipItem() {
        io.SesProject.model.game.item.factory.Weapon sword = new io.SesProject.model.game.item.factory.Weapon("Iron Sword", 10);
        player.addItem(sword);
        player.equipItem(sword);

        player.unequipItem(sword);

        assertNull(player.getEquippedWeapon());
        assertTrue(player.getInventory().contains(sword));
        assertEquals(5, player.getAttackPower()); // Back to base
    }

    @Test
    void testSaveAndRestore() {
        player.addExperience(50); // Should be level 2 (20xp cost) with 30xp remainder
        player.modifyKarma(20);
        player.setPosition(100f, 200f);

        io.SesProject.model.game.item.factory.Weapon sword = new io.SesProject.model.game.item.factory.Weapon("Epic Sword", 50);
        player.addItem(sword);
        player.equipItem(sword);

        // Save
        PlayerCharacterMemento memento = player.save();

        // Modify current player to ensure restore works
        player = new PlayerCharacter("New", "Mage");
        player.restore(memento);

        // Verify
        assertEquals("Hero", player.getName());
        assertEquals("Warrior", player.getArchetype());
        assertEquals(2, player.getLevel()); // 50xp total: lvl 1->2 (20xp), remain 30. Lvl 2->3 needs 40.
        assertEquals(30, player.getExperience());
        assertEquals(30, player.getKarma()); // 10 base + 20 added
        assertEquals(100f, player.getX());
        assertEquals(200f, player.getY());

        assertNotNull(player.getEquippedWeapon());
        assertEquals("Epic Sword", player.getEquippedWeapon().getName());
    }
}
