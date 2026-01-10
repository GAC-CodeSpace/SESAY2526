package io.SesProject.controller.command;



import io.SesProject.controller.PauseMenuController;

public class ResumeGameCommand implements Command {
    private PauseMenuController receiver;

    public ResumeGameCommand(PauseMenuController receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.resumeGame();
    }
}
