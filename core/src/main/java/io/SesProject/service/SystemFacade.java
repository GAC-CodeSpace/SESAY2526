package io.SesProject.service;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.SesProject.model.SettingsService;
import io.SesProject.model.observer.SettingsHandler;

public class SystemFacade {

    private AudioManager audioManager;
    private SettingsService settingsService;
    private SaveService saveService;

    // Gestore nativo di LibGDX per il caricamento asincrono
    private AssetManager assetManager;

    public void initializeSystems() {
        System.out.println("[SYSTEM] Initializing Subsystems...");

        // 1. Inizializza l'AssetManager
        this.assetManager = new AssetManager();

        // Mettiamo in coda il caricamento della Skin (file pesante)
        // Nota: Assicurati che "uiskin.json" sia nella cartella assets
        assetManager.load("uiskin.json", Skin.class);

        // Se avessimo texture del giocatore, le caricheremmo qui:
        // assetManager.load("images/player.png", Texture.class);

        // 2. Audio Service
        this.audioManager = new AudioManager();

        // 3. Settings Service
        this.settingsService = new SettingsService();
        SettingsHandler handler = new SettingsHandler(audioManager);
        this.settingsService.addObserver(handler);
        this.settingsService.applySettings();

        // 4. Save Service
        this.saveService = new SaveService();
    }

    /**
     * Metodo chiamato ciclicamente dallo stato InitState.
     * @return true se il caricamento è finito, false se sta ancora caricando.
     */
    public boolean updateAssetLoading() {
        // update() restituisce true solo quando ha finito di caricare tutto
        return assetManager.update();
    }

    public void shutdownSystems() {
        System.out.println("[SYSTEM] Shutting down...");
        if (audioManager != null) audioManager.dispose();

        // Importante: Scarica tutte le texture dalla memoria video
        if (assetManager != null) assetManager.dispose();
    }

    // --- Getters ---
    public AssetManager getAssetManager() { return assetManager; }
    public AudioManager getAudioManager() { return audioManager; }
    public SettingsService getSettingsService() { return settingsService; }
    public SaveService getSaveService() { return saveService; }

    /**
     * Helper per ottenere la skin una volta caricata.
     * Da usare nelle View.
     */
    public Skin getSkin() {
        // Recupera la skin caricata dall'asset manager
        if (assetManager.isLoaded("uiskin.json")) {
            return assetManager.get("uiskin.json", Skin.class);
        }
        return null; // O gestisci errore se non è ancora carica
    }
}
