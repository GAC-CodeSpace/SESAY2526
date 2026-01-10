package io.SesProject.controller.command;
import io.SesProject.controller.PauseMenuController;

public class QuitToMainCommand implements Command {
    private PauseMenuController receiver;

    public QuitToMainCommand(PauseMenuController receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.quitToMain();
    }
}
