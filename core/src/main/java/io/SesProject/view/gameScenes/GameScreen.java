package io.SesProject.view.gameScenes;


import io.SesProject.controller.GameController;
import io.SesProject.view.BaseMenuScreen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import io.SesProject.model.game.PlayerEntity;


public class GameScreen extends BaseMenuScreen {

    private GameController controller;
    private ShapeRenderer shapeRenderer; // Per disegnare rettangoli di debug

    public GameScreen(GameController controller) {
        super();
        this.controller = controller;
        this.shapeRenderer = new ShapeRenderer();

        // La UI qui è minima (solo HUD eventualmente), il resto è rendering di gioco
        buildUI();
    }

    @Override
    protected void buildUI() {
        // Eventuale HUD o Label di pausa
    }

    @Override
    public void render(float delta) {
        super.render(delta); // Pulisce lo schermo

        // 1. Aggiornamento Logica
        if (controller != null) {
            controller.update(delta);
        }

        // 2. Rendering Gioco
        if (controller != null) {
            shapeRenderer.setProjectionMatrix(stage.getViewport().getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            int index = 0;
            for (PlayerEntity p : controller.getPlayers()) {
                // Colore diverso per P1 e P2
                if (index == 0) shapeRenderer.setColor(Color.RED);   // P1
                else shapeRenderer.setColor(Color.BLUE);             // P2

                // Disegna il personaggio alla posizione x,y aggiornata
                shapeRenderer.rect(p.getX(), p.getY(), p.getWidth(), p.getHeight());

                index++;
            }
            shapeRenderer.end();
        }

        // 3. Rendering UI (Stage) sopra il gioco
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
