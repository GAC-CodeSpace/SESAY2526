package io.SesProject.controller.command.menuCommand;

import io.SesProject.controller.PauseMenuController;
import io.SesProject.controller.command.Command;

public class OpenInventoryCommand implements Command {
    private PauseMenuController receiver;
    public OpenInventoryCommand(PauseMenuController receiver) { this.receiver = receiver; }
    @Override public void execute() { receiver.openInventory(); }
}
