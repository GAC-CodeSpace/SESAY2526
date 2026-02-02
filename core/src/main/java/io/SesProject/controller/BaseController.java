package io.SesProject.controller;


import io.SesProject.RpgGame;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;

public abstract class BaseController {
    protected RpgGame game;
    protected AuthService authService;
    protected BaseMenuScreen view; // Il prodotto creato dalla factory

    public BaseController(RpgGame game, AuthService authService) {
        this.game = game;
        this.authService = authService;
    }

    // --- FACTORY METHOD ---
    // Ogni controller concreto deve dire quale View creare
    protected abstract BaseMenuScreen createView();

    // Metodo template per mostrare la view
    public void show() {
        if (view == null) {
            view = createView();
        }
        game.setScreen(view);
    }

    public RpgGame getGame(){return  this.game;}
}
