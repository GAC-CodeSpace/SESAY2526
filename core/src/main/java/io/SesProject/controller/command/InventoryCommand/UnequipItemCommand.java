package io.SesProject.controller.command.InventoryCommand;

import io.SesProject.controller.command.Command;
import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.item.factory.Item;

public class UnequipItemCommand implements Command {
    private PlayerCharacter pc;
    private Item item;
    public UnequipItemCommand(PlayerCharacter pc, Item item) { this.pc = pc; this.item = item; }
    @Override public void execute() { pc.unequipItem(item); }
}
