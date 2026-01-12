package io.SesProject.model.game.movementStrategy;

import io.SesProject.model.game.GameObject;

public class InputMovementStrategy implements MovementStrategy {

    private float speed = 150f; // Velocità in pixel al secondo

    @Override
    public void move(GameObject entity, float delta) {
        // Calcolo nuova posizione basata sulla velocità attuale
        float newX = entity.getX() + (entity.getVelocityX() * speed * delta);
        float newY = entity.getY() + (entity.getVelocityY() * speed * delta);

        entity.setPosition(newX, newY);
    }
}
