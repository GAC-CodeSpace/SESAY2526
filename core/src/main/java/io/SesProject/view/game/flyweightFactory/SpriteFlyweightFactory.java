package io.SesProject.view.game.flyweightFactory;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import io.SesProject.model.game.visualState.VisualState;
import io.SesProject.service.SystemFacade;

import java.util.HashMap;
import java.util.Map;

/**
 * FLYWEIGHT FACTORY:
 * Gestisce le istanze condivise delle animazioni.
 */
public class SpriteFlyweightFactory {

    private Map<String, Animation<TextureRegion>> animationCache = new HashMap<>();
    private SystemFacade systemFacade;

    // Nome del file atlas creato col Texture Packer
    private static final String ATLAS_PATH = "characters/characters.atlas";

    public SpriteFlyweightFactory(SystemFacade systemFacade) {
        this.systemFacade = systemFacade;

        // Assicuriamoci che l'Atlas sia caricato in memoria UNA volta sola
        if (!systemFacade.getAssetManager().isLoaded(ATLAS_PATH)) {
            systemFacade.getAssetManager().load(ATLAS_PATH, TextureAtlas.class);
            systemFacade.getAssetManager().finishLoadingAsset(ATLAS_PATH);
        }
    }

    public Animation<TextureRegion> getAnimation(String spriteName, VisualState state) {
        // Costruiamo la chiave, es: "mage_WALK_DOWN"
        String key = spriteName + "_" + state.name();

        if (!animationCache.containsKey(key)) {
            createAnimationFromAtlas(spriteName, state, key);
        }
        return animationCache.get(key);
    }

    private void createAnimationFromAtlas(String spriteName, VisualState state, String key) {
        // Recupera l'Atlas dalla memoria
        TextureAtlas atlas = systemFacade.getAssetManager().get(ATLAS_PATH, TextureAtlas.class);

        // 1. Costruzione del nome regione
        // Esempio: "warrior" + "_" + "walk_right" -> "warrior_walk_right"
        String regionName = spriteName.toLowerCase() + "_" + state.name().toLowerCase();

        // 2. Ricerca dei frame nell'Atlas
        // Cerca tutte le regioni numerate (es: warrior_walk_right_1, warrior_walk_right_2...)
        Array<TextureAtlas.AtlasRegion> frames = atlas.findRegions(regionName);

        // 3. Controllo Errori (STRICT MODE)
        if (frames.isEmpty()) {
            // Senza placeholder, se l'asset manca è un errore critico.
            Gdx.app.error("SpriteFactory", "ERRORE: Animazione non trovata nell'Atlas: " + regionName);
            Gdx.app.error("SpriteFactory", "Assicurati di aver aggiunto i file PNG corretti e ricreato l'Atlas.");
            return; // Esce senza mettere nulla in cache (restituirà null, attenzione ai NullPointer altrove!)
        }

        // 4. Creazione Animazione
        float frameDuration = 0.15f; // Velocità animazione

        if (state.name().startsWith("IDLE") || frames.size == 1) {
            // Animazione statica (1 solo frame o IDLE)
            Animation<TextureRegion> anim = new Animation<>(0, frames.get(0));
            animationCache.put(key, anim);
        } else {
            // Animazione dinamica (camminata)
            Animation<TextureRegion> anim = new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP_PINGPONG);
            animationCache.put(key, anim);
        }
    }
}
