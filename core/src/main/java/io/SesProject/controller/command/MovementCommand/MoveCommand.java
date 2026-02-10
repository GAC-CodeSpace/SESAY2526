package io.SesProject.controller.command.MovementCommand;

import io.SesProject.controller.command.Command;
import io.SesProject.model.game.GameObject;
import io.SesProject.model.game.PlayerEntity;

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
        if((receiver instanceof PlayerEntity) && ((PlayerEntity) receiver).getArchetype().equals("warrior")){
            receiver.setVelocity(velocityX + 0.75f, velocityY + 0.75f);
        }
        receiver.setVelocity(velocityX, velocityY);
    }
}
