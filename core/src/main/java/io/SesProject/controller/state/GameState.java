package io.SesProject.controller.state;


import io.SesProject.RpgGame;

public interface GameState {
    void enter(RpgGame game);
    void update(RpgGame game, float delta);
    void exit(RpgGame game);
}
