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

    private Music currentMusic;
    private float masterVolume = 1.0f; // Default

    /**
     * Carica e riproduce una musica di sottofondo in loop.
     * Gestisce automaticamente lo stop della traccia precedente.
     */
    public void playMusic(String filePath) {
        // 1. Se c'è già una musica, la fermiamo e la scarichiamo
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        // 2. Controlla esistenza file
        FileHandle file = Gdx.files.internal(filePath);
        if (!file.exists()) {
            System.err.println("[AUDIO] File non trovato: " + filePath);
            return;
        }

        // 3. Carica e avvia
        try {
            currentMusic = Gdx.audio.newMusic(file);
            currentMusic.setLooping(true);
            currentMusic.setVolume(masterVolume); // Applica subito il volume corrente
            currentMusic.play();
            System.out.println("[AUDIO] Riproduzione avviata: " + filePath);
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
