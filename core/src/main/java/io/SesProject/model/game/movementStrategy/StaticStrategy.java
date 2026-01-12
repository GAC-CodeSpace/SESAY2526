package io.SesProject.model.game.movementStrategy;

import io.SesProject.model.game.GameObject;

public class StaticStrategy implements MovementStrategy {
    @Override
    public void move(GameObject entity, float delta) {
        // Nessun movimento: l'oggetto rimane fermo
        entity.setVelocity(0, 0);
    }
}
