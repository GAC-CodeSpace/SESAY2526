package io.SesProject.controller.game.controllerInputStrategy;

import com.badlogic.gdx.Gdx;
import io.SesProject.controller.command.Command;
import io.SesProject.controller.command.MoveCommand;
import io.SesProject.model.game.GameObject;

public class KeyboardInputStrategy implements InputStrategy {

    private GameObject controlledEntity;
    private int keyUp, keyDown, keyLeft, keyRight;

    public KeyboardInputStrategy(GameObject entity, int up, int down, int left, int right) {
        this.controlledEntity = entity;
        this.keyUp = up;
        this.keyDown = down;
        this.keyLeft = left;
        this.keyRight = right;
    }

    @Override
    public void handleInput() {
        float x = 0;
        float y = 0;

        if (Gdx.input.isKeyPressed(keyUp))    y = 1;
        if (Gdx.input.isKeyPressed(keyDown))  y = -1;
        if (Gdx.input.isKeyPressed(keyLeft))  x = -1;
        if (Gdx.input.isKeyPressed(keyRight)) x = 1;

        // Command Pattern: Crea ed esegue il comando
        Command moveCmd = new MoveCommand(controlledEntity, x, y);
        moveCmd.execute();
    }
}
