package io.SesProject.view;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.SesProject.controller.GameOverController;

public class GameOverScreen extends BaseMenuScreen {

    private GameOverController controller;

    public GameOverScreen(GameOverController controller) {
        super();
        this.controller = controller;
        buildUI();
    }

    @Override
    protected void buildUI() {
        Label title = new Label("GAME OVER", skin);
        title.setFontScale(3.0f);
        title.setColor(Color.RED);

        rootTable.add(title).padBottom(50).row();

        TextButton retryBtn = new TextButton("CARICA SALVATAGGIO", skin);
        TextButton menuBtn = new TextButton("MENU PRINCIPALE", skin);

        rootTable.add(retryBtn).width(300).padBottom(20).row();
        rootTable.add(menuBtn).width(300).row();

        // Listeners
        retryBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                controller.retryLastSave();
            }
        });

        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                controller.returnToMainMenu();
            }
        });
    }
}
