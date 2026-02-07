package io.SesProject.model.game;

import io.SesProject.RpgGame;
import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.movementStrategy.InputMovementStrategy;


public class PlayerEntity extends GameObject {

    private PlayerCharacter data; // DTO Persistente
    private RpgGame game; // Reference to game for audio access

    // Footstep sound timing
    private float footstepTimer = 0f;
    private static final float FOOTSTEP_INTERVAL = 0.35f; // Time between footstep sounds

    public PlayerEntity(PlayerCharacter data, RpgGame game) {
        super();
        this.data = data;
        this.game = game;

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

        // 3. Gestione suono passi
        boolean isMoving = Math.abs(velocityX) > 0 || Math.abs(velocityY) > 0;

        if (isMoving) {
            footstepTimer += delta;

            // Play footstep sound at intervals
            if (footstepTimer >= FOOTSTEP_INTERVAL) {
                playFootstepSound();
                footstepTimer = 0f; // Reset timer
            }
        } else {
            // Reset timer when not moving
            footstepTimer = 0f;
        }
    }

    /**
     * Plays the footstep sound effect
     */
    private void playFootstepSound() {
        if (game != null && game.getSystemFacade() != null) {
            try {
                game.getSystemFacade().getAudioManager().playSound(
                    "music/sfx/playerWalk/03_Step_grass_03.wav",
                    game.getSystemFacade().getAssetManager()
                );
            } catch (Exception e) {
                // Silently handle missing audio file
                // This prevents crashes if the audio file hasn't been added yet
            }
        }
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
