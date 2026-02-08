package io.SesProject.controller;

import io.SesProject.RpgGame;
import io.SesProject.controller.state.PlayState;
import io.SesProject.model.game.combat.CombatReward;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.VictoryScreen;

/**
 * Controller for the Victory Screen shown after winning a combat.
 */
public class VictoryController extends BaseController {

    private CombatReward reward;

    public VictoryController(RpgGame game, AuthService authService, CombatReward reward) {
        super(game, authService);
        this.reward = reward;
    }

    @Override
    protected BaseMenuScreen createView() {
        return new VictoryScreen(this);
    }

    /**
     * Called when user clicks "Continue" button.
     * Returns to the game world.
     */
    public void continueToGame() {
        System.out.println("[VICTORY] Returning to exploration...");
        game.changeAppState(new PlayState());
    }

    public CombatReward getReward() {
        return reward;
    }
}
