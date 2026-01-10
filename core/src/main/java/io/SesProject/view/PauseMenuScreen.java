package io.SesProject.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.SesProject.controller.PauseMenuController;
import io.SesProject.model.menu.MenuComponent;

public class PauseMenuScreen extends BaseMenuScreen {

    @SuppressWarnings("unused")
    private PauseMenuController controller;
    private MenuComponent menuRoot;

    public PauseMenuScreen(PauseMenuController controller, MenuComponent menuRoot) {
        super();
        this.controller = controller;
        this.menuRoot = menuRoot;

        // Background semi-trasparente per effetto pausa sopra il gioco?
        // Per ora usiamo lo standard BaseMenuScreen
        buildUI();
    }

    @Override
    protected void buildUI() {
        if (menuRoot == null) return;

        Label title = new Label(menuRoot.getName(), skin);
        title.setFontScale(1.5f);
        rootTable.add(title).padBottom(40).row();

        for (MenuComponent child : menuRoot.getChildren()) {
            TextButton btn = new TextButton(child.getName(), skin);

            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    child.select();
                }
            });

            rootTable.add(btn).width(250).padBottom(15).row();
        }
    }

    @Override
    public void render(float delta) {
        // Possiamo aggiungere logica per tasto ESC per tornare al gioco
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            controller.resumeGame();
        }
        super.render(delta);
    }
}
