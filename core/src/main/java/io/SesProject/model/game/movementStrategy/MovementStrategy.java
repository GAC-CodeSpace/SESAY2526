package io.SesProject.model.game.movementStrategy;

import io.SesProject.model.game.GameObject;

public interface MovementStrategy {
    void move(GameObject entity, float delta);
}
