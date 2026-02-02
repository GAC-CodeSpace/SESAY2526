package io.SesProject.controller.command.InventoryCommand;

import io.SesProject.controller.InventoryController;
import io.SesProject.controller.command.Command;
import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.item.factory.Item;

public class EquipItemCommand implements Command {
    private InventoryController controller; // Riferimento al controller
    private PlayerCharacter pc;
    private Item item;

    // Costruttore aggiornato
    public EquipItemCommand(InventoryController controller, PlayerCharacter pc, Item item) {
        this.controller = controller;
        this.pc = pc;
        this.item = item;
    }

    @Override
    public void execute() {
        // Delega al controller (che farà partire il suono)
        controller.performEquip(pc, item);
    }
}
