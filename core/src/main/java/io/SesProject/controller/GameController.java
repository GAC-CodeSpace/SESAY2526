package io.SesProject.controller;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.SesProject.RpgGame;
import io.SesProject.controller.game.controllerInputStrategy.InputStrategy;
import io.SesProject.controller.game.controllerInputStrategy.KeyboardInputStrategy;
import io.SesProject.model.GameSession;
import io.SesProject.model.game.PlayerEntity;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.gameScenes.GameScreen;

import java.util.ArrayList;
import java.util.List;


public class GameController extends BaseController {

    private List<PlayerEntity> players;
    private List<InputStrategy> inputStrategies;

    public GameController(RpgGame game, AuthService authService) {
        super(game, authService);
        this.players = new ArrayList<>();
        this.inputStrategies = new ArrayList<>();

        initializeGame();
    }

    private void initializeGame() {
        GameSession session = game.getCurrentSession();
        if (session == null) return;

        // --- PLAYER 1 (WASD) ---
        PlayerEntity p1 = new PlayerEntity(session.getP1());
        players.add(p1);
        inputStrategies.add(new KeyboardInputStrategy(p1,
            Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D));

        // --- PLAYER 2 (ARROWS) ---
        PlayerEntity p2 = new PlayerEntity(session.getP2());
        players.add(p2);
        inputStrategies.add(new KeyboardInputStrategy(p2,
            Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT));
    }

    @Override
    protected BaseMenuScreen createView() {
        return new GameScreen(this);
    }

    public void update(float delta) {
        // 1. Input Strategies (Controller Layer)
        for (InputStrategy s : inputStrategies) {
            s.handleInput();
        }

        // 2. Logic Updates (Model Layer)
        for (PlayerEntity p : players) {
            p.update(delta);
        }

        // 3. System Input
        handleSystemInput();
    }

    private void handleSystemInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.changeController(new PauseMenuController(game, authService));
        }
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }
}
