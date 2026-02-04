package io.SesProject.view.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import io.SesProject.controller.GameController;
import io.SesProject.model.game.GameObject;
import io.SesProject.model.game.npc.NpcData;
import io.SesProject.model.game.visualState.VisualState;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.game.flyweightFactory.SpriteFlyweightFactory;

public class GameScreen extends BaseMenuScreen {

    private GameController controller;
    private SpriteFlyweightFactory spriteFactory;

    public GameScreen(GameController controller) {
        super();
        this.controller = controller;
        // Inizializza la factory passandogli la facade
        this.spriteFactory = new SpriteFlyweightFactory(controller.getGame().getSystemFacade());

        // Imposta l'input processor subito
        Gdx.input.setInputProcessor(stage);

        buildUI();
    }

    // --- FIX FONDAMENTALE PER L'INPUT ---
    // Questo metodo viene chiamato automaticamente da LibGDX quando la schermata diventa attiva.
    // Serve perché se torni dal Menu di Pausa, l'input processor potrebbe essere rimasto sul menu vecchio.
    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }

    public void showNpcDialog(NpcData npcData, boolean isHostile) {

        // 1. Blocca l'input nel controller (ferma i player)
        controller.startDialogState();

        // 2. Forza l'Input Processor sullo stage della GameScreen
        Gdx.input.setInputProcessor(stage);

        Dialog dialog = new Dialog("", skin, "dialog") {
            @Override
            protected void result(Object object) {
                // Ritorna il focus al gioco quando il dialogo si chiude
                Gdx.input.setInputProcessor(stage);
            }
        };


        // Controllo di sicurezza: usa lo sfondo semitrasparente solo se esiste nella skin
        if (skin.has("white", com.badlogic.gdx.scenes.scene2d.utils.Drawable.class)) {
            dialog.setBackground(skin.newDrawable("white", 0, 0, 0, 0.8f));
        }

        // Titolo (Nome NPC)
        Label nameLabel = new Label(npcData.getName(), skin);
        nameLabel.setColor(Color.GOLD);
        nameLabel.setFontScale(1.2f);

        // Testo del dialogo
        Label textLabel = new Label(npcData.getDialogue(), skin);
        textLabel.setWrap(true);
        textLabel.setAlignment(Align.center);

        // Layout contenuto
        dialog.getContentTable().add(nameLabel).padTop(20).row();
        dialog.getContentTable().add(textLabel).width(500).pad(20).row();

        // --- BOTTONI ---

        if (isHostile) {
            TextButton fightBtn = new TextButton("COMBATTI!", skin);
            fightBtn.setColor(Color.RED);

            fightBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    dialog.remove(); // Rimuove completamente il dialogo
                    controller.startCombatFromDialog(npcData);
                }
            });
            dialog.getButtonTable().add(fightBtn).width(150).pad(10);

        } else {
            // Opzione Buona
            TextButton goodBtn = new TextButton("Saluta", skin);
            goodBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    dialog.remove(); // Rimuove completamente il dialogo
                    controller.handleDialogChoice(true); // Scelta Buona
                }
            });

            // Opzione Cattiva
            TextButton badBtn = new TextButton("Ignora", skin);
            badBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    dialog.remove(); // Rimuove completamente il dialogo
                    controller.handleDialogChoice(false); // Scelta Cattiva
                }
            });

            dialog.getButtonTable().add(goodBtn).width(200).pad(10);
            dialog.getButtonTable().add(badBtn).width(200).pad(10);
        }

        // Mostra il dialogo centrato e forza il focus
        dialog.show(stage);

        // FIX CRITICO: Forza il focus della tastiera e del mouse sul dialogo
        stage.setKeyboardFocus(dialog);
        stage.setScrollFocus(dialog);
    }

    @Override
    protected void buildUI() {
        // Eventuale HUD di gioco qui
    }

    @Override
    public void render(float delta) {
        super.render(delta); // Pulisce lo schermo (glClear)

        // 1. Logica di Gioco
        if (controller != null) {
            controller.update(delta);

            // 2. Rendering del Mondo (Dietro la UI)
            stage.getBatch().begin();
            // Imposta la matrice corretta per disegnare nel mondo
            stage.getBatch().setProjectionMatrix(stage.getViewport().getCamera().combined);
            stage.getBatch().setColor(Color.WHITE);

            for (GameObject obj : controller.getWorldEntities()) {
                // Recupera stato e dati
                VisualState state = obj.getVisualState();
                float stateTime = obj.getStateTime();
                String spriteName = obj.getSpriteName(); // Metodo polimorfico

                // Flyweight
                Animation<TextureRegion> anim = spriteFactory.getAnimation(spriteName, state);

                if (anim != null) {
                    TextureRegion currentFrame = anim.getKeyFrame(stateTime, true);

                    // Disegna usando le dimensioni logiche dell'oggetto
                    stage.getBatch().draw(currentFrame, obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
                }
            }
            stage.getBatch().end();
        }

        // 3. Rendering della UI (Sopra il mondo)
        // IMPORTANTE: act(delta) fa funzionare le animazioni UI e gli input dei bottoni
        stage.act(delta);
        stage.draw();
    }
}
