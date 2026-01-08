package io.SesProject.controller.state;

import com.badlogic.gdx.Gdx;
import io.SesProject.RpgGame;
import io.SesProject.model.User;
import io.SesProject.model.memento.Memento;

public class ExitState implements GameState {

    @Override
    public void enter(RpgGame game) {
        System.out.println("[STATE] Entering ExitState");
    /*
        // 1. Salvataggio automatico (Interazione con Model e Service)
        User currentUser = game.getCurrentUser();
        if (currentUser != null) {
            System.out.println("[EXIT] Saving user progress...");
            Memento m = currentUser.save();
            game.getSystemFacade().getSaveService().saveGame(m, currentUser.getUsername());
        }*/

        // 2. Pulizia risorse (Interazione con Facade)
        game.getSystemFacade().shutdownSystems();

        // 3. Uscita fisica
        Gdx.app.exit();
    }

    @Override
    public void update(RpgGame game, float delta) {}

    @Override
    public void exit(RpgGame game) {}
}
