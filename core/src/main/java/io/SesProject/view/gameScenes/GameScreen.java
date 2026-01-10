package io.SesProject.view.gameScenes;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import io.SesProject.controller.GameController;
import io.SesProject.view.BaseMenuScreen;

public class GameScreen extends BaseMenuScreen {

    @SuppressWarnings("unused")
    private GameController controller;

    public GameScreen(GameController controller) {
        super();
        this.controller = controller;
        // buildUI() è chiamato qui se hai rimosso la chiamata dalla classe padre come discusso,
        // altrimenti è automatico. Per sicurezza lo metto qui.
        buildUI();
    }

    @Override
    protected void buildUI() {
        // Lasciamo la UI vuota o mettiamo solo un piccolo testo di debug
        // per confermare che siamo nella schermata giusta.
        Label debugLabel = new Label("GAME SESSION ACTIVE (Black Screen Mode)", skin);
        debugLabel.setColor(1, 1, 1, 0.5f); // Bianco semi-trasparente
        rootTable.add(debugLabel);
    }

    @Override
    public void render(float delta) {
        // Pulisce lo schermo
        super.render(delta);

        // CHIAMA IL CONTROLLER PER GLI INPUT
        if (controller != null) {
            controller.handleInput(delta);
        }

        stage.draw();
    }
}
