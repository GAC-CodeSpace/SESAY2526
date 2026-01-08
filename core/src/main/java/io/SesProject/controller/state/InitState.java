package io.SesProject.controller.state;


import io.SesProject.RpgGame;

public class InitState implements GameState {

    @Override
    public void enter(RpgGame game) {
        System.out.println("[STATE] Entering InitState");
        // Delega alla Facade l'inizializzazione tecnica
        game.getSystemFacade().initializeSystems();
    }

    @Override
    public void update(RpgGame game, float delta) {
        // Controlla se gli asset hanno finito di caricare
        if (game.getSystemFacade().updateAssetLoading()) {
            System.out.println("[STATE] Assets Loaded. Switching to PlayState.");
            game.changeAppState(new PlayState());
        }
    }

    @Override
    public void exit(RpgGame game) {
        System.out.println("[STATE] Exiting InitState");
    }
}
