package io.SesProject.controller.command;


import io.SesProject.controller.MainMenuController;

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
