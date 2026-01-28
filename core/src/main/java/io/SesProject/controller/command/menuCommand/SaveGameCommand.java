package io.SesProject.controller.command.menuCommand;


import io.SesProject.controller.PauseMenuController;
import io.SesProject.controller.command.Command;

public class SaveGameCommand implements Command {
    private PauseMenuController receiver;

    public SaveGameCommand(PauseMenuController receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.saveGame();
    }
}
