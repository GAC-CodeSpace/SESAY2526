package io.SesProject.controller;


import io.SesProject.RpgGame;
import io.SesProject.controller.enumsContainer.MenuSource;
import io.SesProject.model.SettingsService;
import io.SesProject.service.AuthService;
import io.SesProject.service.SystemFacade;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.SettingsScreen;

public class SettingsController extends BaseController {

    private MenuSource source;
    private SettingsService settingsService;

    public SettingsController(RpgGame game, AuthService authService, MenuSource source) {
        super(game, authService);
        this.settingsService = game.getSystemFacade().getSettingsService();
        this.source = source;
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

    public void back() {
        System.out.println("[NAV] Torna indietro verso: " + source);

        switch (source) {
            case MAIN_MENU:
                // Torna al Menu Principale
                SystemFacade facade = game.getSystemFacade();

                facade.getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav" , facade.getAssetManager());
                game.changeController(new MainMenuController(game, authService));
                break;

            case PAUSE_MENU:
                // Torna al Menu di Pausa
                // Nota: La musica non cambia (rimane quella del gioco o del menu precedente)


                game.getSystemFacade().getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav" , game.getSystemFacade().getAssetManager());
                game.changeController(new PauseMenuController(game, authService));
                break;
        }
    }

    // Getter per inizializzare la UI con i valori correnti
    public float getCurrentVolume() { return settingsService.getVolume(); }
    public int getCurrentWidth() { return settingsService.getWidth(); }
    public int getCurrentHeight() { return settingsService.getHeight(); }
}
