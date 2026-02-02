package io.SesProject.service;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

/**
 * Service che gestisce il sottosistema Audio (Facade Pattern).
 * Isola la logica di LibGDX dal resto del gioco.
 */
public class AudioManager implements Disposable {

    private String currentMusicPath;
    private Music currentMusic;
    private float masterVolume = 1.0f; // Default

    /**
     * Carica e riproduce una musica di sottofondo in loop.
     * Gestisce automaticamente lo stop della traccia precedente.
     */
    public void playMusic(String filePath) {
        // 1. CHECK INTELLIGENTE:
        // Se la canzone richiesta è già quella che sta suonando, non fare nulla.
        if (currentMusicPath != null && currentMusicPath.equals(filePath)) {
            return;
        }

        // 2. Stop della musica precedente (se diversa)
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        // 3. Caricamento nuova musica
        FileHandle file = Gdx.files.internal(filePath);
        if (!file.exists()) {
            System.err.println("[AUDIO] File non trovato: " + filePath);
            return;
        }

        try {
            currentMusic = Gdx.audio.newMusic(file);
            currentMusic.setLooping(true);
            currentMusic.setVolume(masterVolume);
            currentMusic.play();

            // Aggiorniamo la traccia corrente
            this.currentMusicPath = filePath;
            System.out.println("[AUDIO] Nuova traccia avviata: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Aggiorna il volume master.
     * Se c'è musica in riproduzione, aggiorna il volume in tempo reale.
     */
    public void setMasterVolume(float volume) {
        this.masterVolume = volume;
        // Aggiornamento realtime
        if (currentMusic != null) {
            currentMusic.setVolume(masterVolume);
        }
    }

    /**
     * Riproduce un effetto sonoro "usa e getta".
     */
    public void playSound(String filePath, AssetManager assetManager) {
        if (assetManager.isLoaded(filePath)) {
            Sound sound = assetManager.get(filePath, Sound.class);
            // Riproduce il suono con il volume corrente
            sound.play(masterVolume + 0.5f);
        } else {
            System.err.println("[AUDIO] SFX non caricato: " + filePath);
        }
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    @Override
    public void dispose() {
        if (currentMusic != null) {
            currentMusic.dispose();
        }
    }
}
