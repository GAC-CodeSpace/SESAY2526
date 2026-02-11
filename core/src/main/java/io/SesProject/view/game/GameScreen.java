package io.SesProject.view.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import io.SesProject.controller.GameController;
import io.SesProject.model.game.GameObject;
import io.SesProject.model.game.map.GameMap;
import io.SesProject.model.game.npc.NpcData;
import io.SesProject.model.game.visualState.VisualState;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.game.flyweightFactory.SpriteFlyweightFactory;

public class GameScreen extends BaseMenuScreen {

    private GameController controller;
    private SpriteFlyweightFactory spriteFactory;

    //Camera dedicata per il rendering del gioco con zoom
    private OrthographicCamera gameCamera;

    //Batch separato per il mondo di gioco (mappa + entità)
    private SpriteBatch worldBatch;

    public GameScreen(GameController controller) {
        super();
        this.controller = controller;
        // Inizializza la factory passandogli la facade
        this.spriteFactory = new SpriteFlyweightFactory(controller.getGame().getSystemFacade());

        // 🎯 Crea un batch separato per il mondo di gioco
        this.worldBatch = new SpriteBatch();

        // 🎯 Crea una camera dedicata per il gioco
        gameCamera = new OrthographicCamera();
        gameCamera.setToOrtho(false, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());

        // 🎯 MODIFICATO: Zoom dinamico basato sul nome della mappa
        float zoomLevel = 0.6f; // Valore di default

        // 🎯 AGGIUNGI: Centra la camera sulla mappa e imposta lo zoom
        try {
            if (controller.getMapController() != null && controller.getMapController().getCurrentMap() != null) {
                GameMap map = controller.getMapController().getCurrentMap();
                String mapName = map.getMapName();

                // 🎯 Decidi lo zoom in base al nome della mappa
                if (mapName != null) {
                    if (mapName.contains("primo_villaggio")) {
                        zoomLevel = 0.6f; // Zoom
                    } else if (mapName.contains("Dungeon_1")) {
                        zoomLevel = 0.8f;
                    }else if (mapName.contains("casa")) {
                        zoomLevel = 0.4f;
                    }else if (mapName.contains("prima_caverna")) {
                        zoomLevel = 0.6f;
                    }else if (mapName.contains("Sala_Boss")) {
                        zoomLevel = 0.6f;
                    }
                    System.out.println("[GameScreen] Setting zoom " + zoomLevel + " for map: " + mapName);
                }

                Rectangle bounds = map.getActualBounds();

                // Posiziona la camera al centro della mappa
                gameCamera.position.set(
                    bounds.x + bounds.width / 2f,
                    bounds.y + bounds.height / 2f,
                    0
                );
            }
        } catch (Exception e) {
            System.err.println("[GameScreen] Could not center camera on map: " + e.getMessage());
        }

        gameCamera.zoom = zoomLevel;
        gameCamera.update();

        // Imposta l'input processor subito
        Gdx.input.setInputProcessor(stage);

        buildUI();

        // Show story introduction for new games
        if (controller.getGame().getCurrentSession() != null &&
            controller.getGame().getCurrentSession().isNewGame()) {
            showStoryIntroduction();
        }
    }

    /**
     * Shows the story introduction dialog for new games
     */
    private void showStoryIntroduction() {
        // Block input in controller
        controller.startDialogState();

        // Force input processor to stage
        Gdx.input.setInputProcessor(stage);

        Dialog dialog = new Dialog("", skin, "dialog");

        // Semi-transparent background
        if (skin.has("white", com.badlogic.gdx.scenes.scene2d.utils.Drawable.class)) {
            dialog.setBackground(skin.newDrawable("white", 0, 0, 0, 0.9f));
        }

        // Title
        Label titleLabel = new Label("L'Inizio della Leggenda", skin);
        titleLabel.setColor(Color.GOLD);
        titleLabel.setFontScale(1.5f);
        dialog.getContentTable().add(titleLabel).padTop(30).padBottom(20).row();

        // Story text
        String[] storyLines = {
            "Ascolta, mia Progenie.",
            "",
            "Un'ombra terribile è calata su Smede.",
            "Il cuore del mondo ha smesso di battere e il gelo avanza:",
            "le Forgie si stanno spegnendo una dopo l'altra.",
            "",
            "Non c'è tempo per la paura.",
            "Il destino del creato è ora nelle tue mani.",
            "",
            "Viaggia attraverso i regni, raggiungi le 4 Forgie Elementali",
            "e recupera l'Arte della Riaccensione prima che l'oscurità",
            "ci inghiotta per sempre.",
            "",
            "Va', ora."
        };

        for (String line : storyLines) {
            Label lineLabel = new Label(line, skin);
            lineLabel.setWrap(true);
            lineLabel.setAlignment(Align.center);

            if (line.isEmpty()) {
                dialog.getContentTable().add(lineLabel).width(600).padBottom(5).row();
            } else if (line.equals("Ascolta, mia Progenie.")) {
                lineLabel.setColor(Color.CYAN);
                lineLabel.setFontScale(1.1f);
                dialog.getContentTable().add(lineLabel).width(600).padBottom(15).row();
            } else if (line.equals("Va', ora.")) {
                lineLabel.setColor(Color.ORANGE);
                lineLabel.setFontScale(1.1f);
                dialog.getContentTable().add(lineLabel).width(600).padTop(10).row();
            } else {
                lineLabel.setColor(Color.WHITE);
                dialog.getContentTable().add(lineLabel).width(600).padBottom(3).row();
            }
        }

        // Continue button
        TextButton continueBtn = new TextButton("INIZIA AVVENTURA", skin);
        continueBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Clear the new game flag so story doesn't show again
                controller.getGame().getCurrentSession().setIsNewGame(false);
                // End dialog state to restore input
                controller.endDialogState();
                // Remove the dialog
                dialog.remove();
            }
        });
        dialog.getButtonTable().add(continueBtn).width(250).height(50).padTop(20).padBottom(20);

        // Show dialog
        dialog.show(stage);
        stage.setKeyboardFocus(dialog);
        stage.setScrollFocus(dialog);
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
        Label nameLabel = new Label(npcData.getDisplayName(), skin);
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
            TextButton goodBtn = new TextButton("Aiuta", skin);
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

            if (gameCamera != null) {
                gameCamera.update();
            }
            // 2. Rendering del Mondo (Dietro la UI) - Usa worldBatch separato
            worldBatch.begin();

            // 🎯 MODIFICATO: Usa la gameCamera con lo zoom
            worldBatch.setProjectionMatrix(gameCamera.combined);
            worldBatch.setColor(Color.WHITE);

            // Render the map first (lowest z-depth)
            if (controller.getMapController() != null) {
                GameMap map = controller.getMapController().getCurrentMap();
                if (map != null) {
                    try{
                        map.render(worldBatch);
                    } catch (Exception e) {
                        System.err.println("[GameScreen] Error rendering map: " + e.getMessage());
                    }
                }
            }

// IMPORTANTE: Non cambiare la projection matrix qui!
// Gli sprite usano la STESSA projection matrix della mappa

// Then render entities on top of the map
            try {
                for (GameObject obj : controller.getWorldEntities()) {
                    // Recupera stato e dati
                    VisualState state = obj.getVisualState();
                    float stateTime = obj.getStateTime();
                    String spriteName = obj.getSpriteName();

                    // Flyweight
                    Animation<TextureRegion> anim = spriteFactory.getAnimation(spriteName, state);

                    if (anim != null) {
                        TextureRegion currentFrame = anim.getKeyFrame(stateTime, true);

                        // Disegna usando le dimensioni logiche dell'oggetto
                        worldBatch.draw(currentFrame, obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
                    }
                }
            } catch (Exception e) {
                System.err.println("[GameScreen] Error rendering entities: " + e.getMessage());
            }


            worldBatch.end();
        }


        // 3. Rendering della UI (Sopra il mondo)
        // IMPORTANTE: act(delta) fa funzionare le animazioni UI e gli input dei bottoni
        stage.act(delta);
        stage.draw();
    }

    // 🎯 AGGIUNTO: Gestisci il resize per mantenere lo zoom
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        // Aggiorna anche la gameCamera quando la finestra viene ridimensionata
        if (gameCamera != null) {
            gameCamera.setToOrtho(false, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());

            // 🎯 MODIFICATO: Ricalcola lo zoom in base alla mappa corrente
            float zoomLevel = 0.6f;

            try {
                if (controller.getMapController() != null && controller.getMapController().getCurrentMap() != null) {
                    GameMap map = controller.getMapController().getCurrentMap();
                    String mapName = map.getMapName();

                    if (mapName != null) {
                        if (mapName.contains("primo_villaggio")) {
                            zoomLevel = 0.6f;
                        } else if (mapName.contains("Dungeon_1")) {
                            zoomLevel = 0.8f;
                        }else if (mapName.contains("casa")) {
                            zoomLevel = 0.4f;
                        }else if (mapName.contains("prima_caverna")) {
                            zoomLevel = 0.6f;
                        }else if (mapName.contains("Sala_Boss")) {
                            zoomLevel = 0.6f;
                        }
                    }

                    Rectangle bounds = map.getActualBounds();

                    gameCamera.position.set(
                        bounds.x + bounds.width / 2f,
                        bounds.y + bounds.height / 2f,
                        0
                    );
                }
            } catch (Exception e) {
                System.err.println("[GameScreen] Could not center camera on resize: " + e.getMessage());
            }

            gameCamera.zoom = zoomLevel;
            gameCamera.update();
        }
    }
    /**
     * Aggiorna lo zoom della camera quando viene caricata una nuova mappa
     */
    public void updateCameraForCurrentMap() {
        if (gameCamera == null) return;

        float zoomLevel = 0.6f; // Default

        try {
            if (controller.getMapController() != null && controller.getMapController().getCurrentMap() != null) {
                GameMap map = controller.getMapController().getCurrentMap();
                String mapName = map.getMapName();

                if (mapName != null) {
                    if (mapName.contains("primo_villaggio")) {
                        zoomLevel = 0.6f;
                    } else if (mapName.contains("Dungeon_1")) {
                        zoomLevel = 0.8f;
                    }else if (mapName.contains("casa")) {
                        zoomLevel = 0.4f;
                    }else if (mapName.contains("prima_caverna")) {
                        zoomLevel = 0.6f;
                    }else if (mapName.contains("Sala_Boss")) {
                        zoomLevel = 0.6f;
                    }
                    System.out.println("[GameScreen] Updating zoom to " + zoomLevel + " for map: " + mapName);
                }

                Rectangle bounds = map.getActualBounds();

                // Riposiziona la camera al centro della nuova mappa
                gameCamera.position.set(
                    bounds.x + bounds.width / 2f,
                    bounds.y + bounds.height / 2f,
                    0
                );
            }
        } catch (Exception e) {
            System.err.println("[GameScreen] Could not update camera: " + e.getMessage());
        }

        gameCamera.zoom = zoomLevel;
        gameCamera.update();
    }
    public GameController getController() {
        return this.controller;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (worldBatch != null) {
            worldBatch.dispose();
        }
    }
}
