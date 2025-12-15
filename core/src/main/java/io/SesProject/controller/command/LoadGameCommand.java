package io.SesProject.controller.command;


import io.SesProject.controller.MainMenuController;

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
