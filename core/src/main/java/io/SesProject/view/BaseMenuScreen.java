package io.SesProject.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class BaseMenuScreen implements Screen {
    protected Stage stage;
    protected Table rootTable;
    protected Skin skin;

    public BaseMenuScreen() {
        this.stage = new Stage(new ScreenViewport());
        // Assicurati di avere uiskin.json/atlas/png nella cartella assets!
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        this.rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Gestione Input
        Gdx.input.setInputProcessor(stage);


    }

    public void showMessage(String title, String message) {
        Dialog dialog = new Dialog(title, skin) {
            @Override
            protected void result(Object object) {
                // Codice eseguito quando si preme il tasto (es. chiudi)
                // Di default hide() viene chiamato automaticamente.
            }
        };

        dialog.text(message);
        dialog.button("OK", true); // Aggiunge un bottone OK che ritorna 'true'

        // Opzionale: Animazione e dimensione
        dialog.show(stage);
        dialog.getContentTable().pad(10);
        dialog.getButtonTable().pad(10);
    }




    protected abstract void buildUI();

    @Override
    public void render(float delta) {
        // Pulisce lo schermo (Grigio scuro)
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
