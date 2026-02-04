package io.SesProject.model.game;

import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.movementStrategy.InputMovementStrategy;


public class PlayerEntity extends GameObject {

    private PlayerCharacter data; // DTO Persistente

    public PlayerEntity(PlayerCharacter data) {
        super();
        this.data = data;

        // Inizializza posizione dai dati salvati
        this.x = data.getX();
        this.y = data.getY();

        // Imposta strategia di movimento (Movable)
        this.setMovementStrategy(new InputMovementStrategy());
    }

    @Override
    public void update(float delta) {
        // 1. Muovi l'oggetto (logica GameObject -> Strategy)
        super.update(delta);

        // 2. Sincronizza i dati persistenti
        data.setPosition(this.x, this.y);
    }

    public String getName() {
        return data.getName();
    }

    public String getArchetype() {
       return this.data.getArchetype();
    }

    public PlayerCharacter getData() {
        return this.data;
    }

    @Override
    public String getSpriteName() {
        // Restituisce l'archetipo (es. "warrior", "mage")
        return data.getArchetype();
    }
}
