package io.SesProject.controller.command;


import io.SesProject.controller.MainMenuController;



import io.SesProject.controller.MainMenuController;
import io.SesProject.controller.PauseMenuController;

public class OpenSettingsCommand implements Command {

    private MainMenuController mainReceiver;
    private PauseMenuController pauseReceiver;

    // Costruttore per Main Menu
    public OpenSettingsCommand(MainMenuController receiver) {
        this.mainReceiver = receiver;
    }

    // Costruttore per Pause Menu
    public OpenSettingsCommand(PauseMenuController receiver) {
        this.pauseReceiver = receiver;
    }

    @Override
    public void execute() {
        if (mainReceiver != null) {
            mainReceiver.goToSettings();
        } else if (pauseReceiver != null) {
            pauseReceiver.goToSettings();
        }
    }
}
