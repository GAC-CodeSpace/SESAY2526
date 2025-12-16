package io.SesProject.controller;


import io.SesProject.RpgGame;
import io.SesProject.model.SettingsService;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.SettingsScreen;

public class SettingsController extends BaseController {

    private SettingsService settingsService;

    public SettingsController(RpgGame game, AuthService authService) {
        super(game, authService);
        this.settingsService = game.getSettingsService();
    }

    @Override
    protected BaseMenuScreen createView() {
        return new SettingsScreen(this);
    }


    public void updateVolume(float volume) {
        settingsService.setVolume(volume);
    }

    /**
     * Aggiorna la risoluzione parsando la stringa della UI.
     * @param resolutionStr Formato atteso "1920x1080"
     */
    public void updateResolution(String resolutionStr) {
        try {
            String[] parts = resolutionStr.split("x");
            int w = Integer.parseInt(parts[0]);
            int h = Integer.parseInt(parts[1]);
            settingsService.setResolution(w, h);
        } catch (Exception e) {
            System.err.println("Errore parsing risoluzione: " + resolutionStr);
        }
    }

    public void backToMain() {
        // Torna al Main Menu usando il controller esistente
        game.changeController(new MainMenuController(game, authService));
    }

    // Getter per inizializzare la UI con i valori correnti
    public float getCurrentVolume() { return settingsService.getVolume(); }
    public int getCurrentWidth() { return settingsService.getWidth(); }
    public int getCurrentHeight() { return settingsService.getHeight(); }
}
