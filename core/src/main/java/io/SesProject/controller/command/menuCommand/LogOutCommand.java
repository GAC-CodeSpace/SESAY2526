package io.SesProject.controller.command.menuCommand;

import io.SesProject.controller.MainMenuController;
import io.SesProject.controller.command.Command;

public class LogOutCommand implements Command {
    MainMenuController reciver;

    public LogOutCommand(MainMenuController reciver){this.reciver = reciver;}
    @Override
    public void execute() {
        reciver.logout();
    }
}
