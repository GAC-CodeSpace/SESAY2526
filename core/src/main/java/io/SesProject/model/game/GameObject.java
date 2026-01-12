package io.SesProject.model.game;


import io.SesProject.model.game.movementStrategy.MovementStrategy;
import io.SesProject.model.game.movementStrategy.StaticStrategy;

public abstract class GameObject {

    protected float x;
    protected float y;
    protected float width, height;
    protected float velocityX, velocityY;

    // Riferimento alla Strategy
    private MovementStrategy strategy;

    public GameObject() {
        // Default safe: immobile
        this.strategy = new StaticStrategy();
        this.width = 32; // Default size
        this.height = 32;
    }

    public void update(float delta) {
        if (strategy != null) {
            strategy.move(this, delta);
        }
    }

    // --- Setters & Getters ---
    public void setMovementStrategy(MovementStrategy strategy) {
        this.strategy = strategy;
    }

    public void setVelocity(float vx, float vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
