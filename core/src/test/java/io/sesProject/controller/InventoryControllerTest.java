package io.sesProject.controller;

import io.SesProject.controller.InventoryController;
import io.SesProject.model.game.item.ItemType;
import io.SesProject.model.game.item.factory.Item;
import io.SesProject.model.game.item.factory.Weapon;
import io.sesProject.controller.ControllerTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryControllerTest extends ControllerTestBase {

    private InventoryController controller;

    @BeforeEach
    void setUp() {
        super.setUpBase();
        controller = new InventoryController(mockGame, mockAuthService);
    }

    @Test
    void testEquipItem() {
        Item sword = new Weapon("Test Sword", 10);
        p1.addItem(sword);

        controller.performEquip(p1, sword);

        // Verify audio played
        verify(mockAudio).playSound(contains("Equip"), eq(mockAssetManager));

        // Verify model change
        assertEquals(sword, p1.getEquippedWeapon());
    }

    @Test
    void testUnequipItem() {
        Item sword = new Weapon("Test Sword", 10);
        p1.addItem(sword);
        p1.equipItem(sword);

        controller.performUnequip(p1, sword);

        verify(mockAudio).playSound(contains("Unequip"), eq(mockAssetManager));

        assertNull(p1.getEquippedWeapon());
        assertTrue(p1.getInventory().contains(sword));
    }

    @Test
    void testMenuGeneration() {
        assertNotNull(controller.getInventoryTree(p1));
    }
}
