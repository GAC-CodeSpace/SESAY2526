package io.SesProject.controller.state;

import io.SesProject.RpgGame;
import io.SesProject.controller.CombatController;
import io.SesProject.model.game.npc.NpcData;

public class CombatState implements GameState{
    private NpcData targetEnemyData; // Il nemico specifico da affrontare

    // Costruttore Vuoto (Incontro casuale "da erba alta", se previsto)
    public CombatState() {
        this.targetEnemyData = null;
    }

    // Costruttore con Target (Incontro da mappa / Interazione)
    public CombatState(NpcData targetEnemyData) {
        this.targetEnemyData = targetEnemyData;
    }

    @Override
    public void enter(RpgGame game) {
        System.out.println("[STATE] Entering Combat Mode");
        //game.getSystemFacade().getAudioManager().playMusic("music/battle_theme.ogg");

        // Passiamo il nemico specifico al Controller
        CombatController combatController = new CombatController(
            game,
            game.getAuthService(),
            targetEnemyData // <--- PASSAGGIO DATI
        );

        game.changeController(combatController);
    }

    @Override
    public void update(RpgGame game, float delta) {

    }

    @Override
    public void exit(RpgGame game) {
        System.out.println("[STATE] exit from combat state");
    }
}
