package io.SesProject.controller.command.menuCommand;


import io.SesProject.controller.MainMenuController;
import io.SesProject.controller.command.Command;

public class LoadGameCommand implements Command {
    private MainMenuController receiver;

    public LoadGameCommand(MainMenuController receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.loadGame();
    }
}
