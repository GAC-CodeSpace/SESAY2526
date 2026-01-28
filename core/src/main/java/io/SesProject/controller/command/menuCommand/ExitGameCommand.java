package io.SesProject.controller.command.menuCommand;


import io.SesProject.controller.MainMenuController;
import io.SesProject.controller.command.Command;

public class ExitGameCommand implements Command {
    private MainMenuController receiver;

    public ExitGameCommand(MainMenuController receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.exitGame();
    }
}
