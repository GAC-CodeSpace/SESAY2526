package io.SesProject.controller.command.InventoryCommand;

import io.SesProject.controller.InventoryController;
import io.SesProject.controller.command.Command;
import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.item.factory.Item;

public class UsePowerUpCommand implements Command {
    private InventoryController controller;
    private PlayerCharacter pc;
    private Item powerUp;

    public UsePowerUpCommand(InventoryController controller, PlayerCharacter pc, Item powerUp) {
        this.controller = controller;
        this.pc = pc;
        this.powerUp = powerUp;
    }

    @Override
    public void execute() {
        // Chiama il metodo nel controller per gestire la logica di apertura menu
        controller.usePowerUp(pc, powerUp);
    }
}
