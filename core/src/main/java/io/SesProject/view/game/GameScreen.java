package io.SesProject.view.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.SesProject.controller.GameController;
import io.SesProject.model.game.GameObject;
import io.SesProject.model.game.PlayerEntity;
import io.SesProject.model.game.npc.factory.NpcEntity;
import io.SesProject.model.game.visualState.VisualState;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.game.flyweightFactory.SpriteFlyweightFactory;

public class GameScreen extends BaseMenuScreen {

    private GameController controller;
    private SpriteFlyweightFactory spriteFactory; // La nostra factory grafica

    public GameScreen(GameController controller) {
        super();
        this.controller = controller;
        // Inizializza la factory passandogli la facade
        this.spriteFactory = new SpriteFlyweightFactory(controller.getGame().getSystemFacade());
        buildUI();
    }

    @Override
    protected void buildUI() {

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (controller != null) {
            controller.update(delta);

            stage.getBatch().begin();

            for (GameObject obj : controller.getWorldEntities()) {
                // 1. Recupera stato e dati dal Model
                VisualState state = obj.getVisualState();
                float stateTime = obj.getStateTime();

                // --- CORREZIONE: Usa il metodo polimorfico ---
                // Non servono più 'if instanceof' per il nome dello sprite
                String spriteName = obj.getSpriteName();

                // 2. Usa il Flyweight per ottenere l'animazione corretta
                Animation<TextureRegion> anim = spriteFactory.getAnimation(spriteName, state);

                if (anim != null) {
                    // 3. Ottieni il frame e disegna
                    TextureRegion currentFrame = anim.getKeyFrame(stateTime, true);
                    stage.getBatch().draw(currentFrame, obj.getX(), obj.getY());
                } else {
                    // Debug per capire perché non trova l'animazione
                    Gdx.app.error("GameScreen", "Animazione non trovata per: " + spriteName + " con stato " + state);
                }
            }
            stage.getBatch().end();
        }
        stage.draw();
    }
}
