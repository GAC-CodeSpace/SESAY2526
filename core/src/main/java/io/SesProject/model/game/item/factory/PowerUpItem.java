package io.SesProject.model.game.item.factory;

import io.SesProject.model.game.item.ItemType;

public class PowerUpItem extends Item {
    public PowerUpItem() {
        // Passiamo 1 come valore interno (non verrà mostrato ma serve al costruttore di Item)
        super("Sfera del Potere", "Potenzia un oggetto equipaggiato", ItemType.CONSUMABLE, 5);
    }

    @Override
    public String getStatDescription() {
        // Ritorniamo solo la descrizione testuale senza il numero
        return description;
    }
}
