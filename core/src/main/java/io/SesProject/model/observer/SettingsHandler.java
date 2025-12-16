package io.SesProject.model.observer;
import com.badlogic.gdx.Gdx;
import io.SesProject.service.AudioManager;

/**
 * Concrete Observer.
 * Reagisce agli eventi del Model applicando le modifiche al sistema.
 */

/**
 * Concrete Observer.
 * Collega gli eventi del Model (SettingsService) ai sottosistemi concreti (AudioManager, Graphics).
 */
public class SettingsHandler implements SettingsObserver {

    private AudioManager audioManager;

    // Dependency Injection: Passiamo il manager audio
    public SettingsHandler(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    @Override
    public void onVolumeChanged(float volume) {
        System.out.println("[SYSTEM] Update Volume: " + volume);
        // Delega al manager audio
        if (audioManager != null) {
            audioManager.setMasterVolume(volume);
        }
    }

    @Override
    public void onResolutionChanged(int width, int height) {
        System.out.println("[SYSTEM] Update Resolution: " + width + "x" + height);
        if (Gdx.graphics.supportsDisplayModeChange()) {
            Gdx.graphics.setWindowedMode(width, height);
        }
    }
}
