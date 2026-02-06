package io.SesProject.model.game.movementStrategy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.SesProject.model.game.GameObject;

public class RandomMovementStrategy implements MovementStrategy {

    private float moveTimer;
    private float moveDuration;
    private float waitTimer;
    private float waitDuration;
    private boolean isMoving;
    private Vector2 currentDirection;
    private float moveSpeed = 50f; // Velocità di default

    public RandomMovementStrategy() {
        this.currentDirection = new Vector2();
        pickNewState();
    }

    @Override
    public void move(GameObject entity, float delta) {
        if (isMoving) {
            moveTimer -= delta;
            
            // Applica il movimento
            entity.setVelocity(currentDirection.x, currentDirection.y);
            
            // Integrazione posizione (come InputMovementStrategy)
            float newX = entity.getX() + (currentDirection.x * moveSpeed * delta);
            float newY = entity.getY() + (currentDirection.y * moveSpeed * delta);
            entity.setPosition(newX, newY);

            if (moveTimer <= 0) {
                // Passa allo stato di attesa
                isMoving = false;
                entity.setVelocity(0, 0);
                waitDuration = MathUtils.random(1.0f, 3.0f); // Attendi tra 1 e 3 secondi
                waitTimer = waitDuration;
            }
        } else {
            waitTimer -= delta;
            entity.setVelocity(0, 0); // Assicurati che sia fermo

            if (waitTimer <= 0) {
                // Passa allo stato di movimento
                pickNewState();
            }
        }
    }

    private void pickNewState() {
        isMoving = true;
        // Scegli una direzione casuale (normale)
        float angle = MathUtils.random(0f, 360f);
        currentDirection.set(1, 0).setAngleDeg(angle);
        
        // Durata movimento tra 0.5 e 2 secondi
        moveDuration = MathUtils.random(0.5f, 2.0f);
        moveTimer = moveDuration;
    }
}
