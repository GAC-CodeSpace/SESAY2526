package io.SesProject.model.game;

import io.SesProject.model.game.movementStrategy.MovementStrategy;
import io.SesProject.model.game.movementStrategy.StaticStrategy;
import io.SesProject.model.game.visualState.VisualState;

public abstract class GameObject {

    protected float x, y;
    protected float width, height;
    protected float velocityX, velocityY;

    // Strategy Pattern (Movimento Logico) - QUESTO LO TENIAMO
    protected MovementStrategy movementBehavior;

    private VisualState currentVisualState = VisualState.IDLE_DOWN;
    private float stateTime = 0f; // Tempo trascorso nello stato (per l'animazione)

    // Teniamo traccia dell'ultima direzione per l'Idle
    private VisualState lastDirection = VisualState.IDLE_DOWN;

    // Collision property for map interaction
    protected boolean solid = false;

    public GameObject() {
        this.movementBehavior = new StaticStrategy();
        this.width = 24;  // Dimensione del quadrato
        this.height = 24;
    }

    public void update(float delta) {
        // Esegue il movimento fisico
        if (movementBehavior != null) {
            movementBehavior.move(this, delta);

        }

        // Aggiorna timer animazione
        stateTime += delta;
        updateVisualState();
    }

    private void updateVisualState() {
        // Determina se si sta muovendo
        boolean isMoving = Math.abs(velocityX) > 0 || Math.abs(velocityY) > 0;

        if (isMoving) {
            // Determina direzione prioritaria
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                if (velocityX > 0) currentVisualState = VisualState.WALK_RIGHT;
                else currentVisualState = VisualState.WALK_LEFT;
            } else {
                if (velocityY > 0) currentVisualState = VisualState.WALK_UP;
                else currentVisualState = VisualState.WALK_DOWN;
            }
            // Salva l'ultima direzione per quando si ferma
            lastDirection = currentVisualState;
        } else {
            // Fermo: converti l'ultima camminata in Idle corrispondente
            currentVisualState = toIdle(lastDirection);
        }
    }

    private VisualState toIdle(VisualState walkState) {
        switch (walkState) {
            case WALK_UP: return VisualState.IDLE_UP;
            case WALK_LEFT: return VisualState.IDLE_LEFT;
            case WALK_RIGHT: return VisualState.IDLE_RIGHT;
            default: return VisualState.IDLE_DOWN;
        }
    }

    // Getters per la View
    public VisualState getVisualState() { return currentVisualState; }
    public float getStateTime() { return stateTime; }

    // --- Getters e Setters Base ---
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public float getX() { return x; }
    public float getY() { return y; }
    public void setVelocity(float vx, float vy) { this.velocityX = vx; this.velocityY = vy; }
    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public void setMovementStrategy(MovementStrategy b) { this.movementBehavior = b; }

    // Collision methods
    public boolean isSolid() { return solid; }
    public void setSolid(boolean solid) { this.solid = solid; }

    public abstract String getSpriteName();
}
