package io.SesProject.view.game;


import io.SesProject.controller.GameController;
import io.SesProject.model.game.GameObject;
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
        super.render(delta); // Pulisce lo schermo (metodo padre)

        // 1. Aggiornamento Logica (Input + Movimento)
        if (controller != null) {
            controller.update(delta);
        }

        // 2. Rendering Gioco (Mondo)
        if (controller != null) {
            // Imposta la camera
            shapeRenderer.setProjectionMatrix(stage.getViewport().getCamera().combined);

            // INIZIO DISEGNO
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            // Iteriamo su TUTTE le entità (Player + NPC)
            for (GameObject obj : controller.getWorldEntities()) {

                // Logica Colore: Distinguiamo Player da NPC
                if (obj instanceof io.SesProject.model.game.PlayerEntity) {
                    if(((PlayerEntity) obj).getName().equals("Giocatore 1")){
                        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.BLUE);
                    }
                    else{
                        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
                    }

                } else {
                    // È un NPC o altro oggetto
                    shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.GREEN);
                }

                // Disegna il rettangolo
                shapeRenderer.rect(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
            }

            // FINE DISEGNO
            shapeRenderer.end();
        }

        // 3. Rendering UI (Stage) sopra il gioco (Pausa, HUD, ecc.)
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
