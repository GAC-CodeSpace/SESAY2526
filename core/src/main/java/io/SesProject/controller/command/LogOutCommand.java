package io.SesProject.controller.command;

import io.SesProject.controller.MainMenuController;

public class LogOutCommand implements Command{
    MainMenuController reciver;

    public LogOutCommand(MainMenuController reciver){this.reciver = reciver;}
    @Override
    public void execute() {
        reciver.logout();
    }
}
