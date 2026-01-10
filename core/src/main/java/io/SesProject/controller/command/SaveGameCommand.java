package io.SesProject.controller.command;


import io.SesProject.controller.PauseMenuController;

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
