package io.SesProject.controller.command.InventoryCommand;

import io.SesProject.controller.InventoryController;
import io.SesProject.controller.command.Command;
import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.item.factory.Item;

public class UnequipItemCommand implements Command {
    private PlayerCharacter pc;
    private InventoryController controller;
    private Item item;

    public UnequipItemCommand(PlayerCharacter pc, Item item , InventoryController controller) { this.pc = pc; this.item = item; this.controller = controller; }

    @Override
    public void execute() {
        // Delega al controller (che farà partire il suono)
        controller.performUnequip(pc, item);
    }
}
