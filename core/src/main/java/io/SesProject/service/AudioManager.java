package io.SesProject.service;


import com.badlogic.gdx.Gdx;
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
    public void playSound(String filePath) {
        FileHandle file = Gdx.files.internal(filePath);
        if (file.exists()) {
            Sound sound = Gdx.audio.newSound(file);
            // Suona con il volume master e poi si autodistrugge dalla memoria (gestito da GDX)
            long id = sound.play(masterVolume);
            // Nota: per i Sound in produzione servirebbe un AssetManager per non caricarli ogni volta
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
