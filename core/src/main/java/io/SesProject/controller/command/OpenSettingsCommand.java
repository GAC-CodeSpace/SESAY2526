package io.SesProject.controller.command;


import io.SesProject.controller.MainMenuController;

public class OpenSettingsCommand implements Command {
    private MainMenuController receiver;

    public OpenSettingsCommand(MainMenuController receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.goToSettings();
    }
}
