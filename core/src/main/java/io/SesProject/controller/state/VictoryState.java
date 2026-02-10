package io.SesProject.controller.state;

import io.SesProject.RpgGame;
import io.SesProject.controller.VictoryController;
import io.SesProject.model.game.combat.CombatReward;

/**
 * State shown after winning a combat encounter.
 * Displays rewards (XP, Karma, level ups) and then returns to exploration.
 */
public class VictoryState implements GameState {

    private CombatReward reward;

    public VictoryState(CombatReward reward) {
        this.reward = reward;
    }

    @Override
    public void enter(RpgGame game) {
        System.out.println("[STATE] Entering Victory State");

        // Play victory sound
        try {
            game.getSystemFacade().getAudioManager().playSound(
                "music/sfx/encounter/02_Heal_02.wav",
                game.getSystemFacade().getAssetManager()
            );
        } catch (Exception e) {
            System.out.println("[AUDIO] Victory sound not available");
        }

        // Activate the Victory Controller
        VictoryController controller = new VictoryController(game, game.getAuthService(), reward);
        game.changeController(controller);
    }

    @Override
    public void update(RpgGame game, float delta) {
        // No game world update - Game paused on victory screen
    }

    @Override
    public void exit(RpgGame game) {
        System.out.println("[STATE] Exiting Victory State");
    }
}
