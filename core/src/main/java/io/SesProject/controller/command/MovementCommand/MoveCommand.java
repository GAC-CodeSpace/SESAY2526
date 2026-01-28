package io.SesProject.controller.command.MovementCommand;

import io.SesProject.controller.command.Command;
import io.SesProject.model.game.GameObject;

public class MoveCommand implements Command {

    private GameObject receiver;
    private float velocityX;
    private float velocityY;

    public MoveCommand(GameObject receiver, float vx, float vy) {
        this.receiver = receiver;
        this.velocityX = vx;
        this.velocityY = vy;
    }

    @Override
    public void execute() {
        receiver.setVelocity(velocityX, velocityY);
    }
}
