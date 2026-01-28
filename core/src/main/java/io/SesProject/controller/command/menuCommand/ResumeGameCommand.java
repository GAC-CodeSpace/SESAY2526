package io.SesProject.controller.command.menuCommand;



import io.SesProject.controller.PauseMenuController;
import io.SesProject.controller.command.Command;

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
