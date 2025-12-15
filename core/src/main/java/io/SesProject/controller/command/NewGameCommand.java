package io.SesProject.controller.command;


import io.SesProject.controller.MainMenuController;

public class NewGameCommand implements Command {
    private MainMenuController receiver;

    public NewGameCommand(MainMenuController receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.newGame();
    }
}
