package io.SesProject.view;



import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.SesProject.controller.MainMenuController;


public class MainMenuScreen extends BaseMenuScreen {

    private MainMenuController controller;

    public MainMenuScreen(MainMenuController controller) {
        super();
        this.controller = controller;
        setupListeners();
    }

    @Override
    protected void buildUI() {
        // Possiamo personalizzare il titolo con il nome utente se vogliamo
        // Ma per ora teniamo un titolo generico
        rootTable.add(new Label("MENU PRINCIPALE", skin)).padBottom(40).row();

        // 1. Gioca
        TextButton playBtn = new TextButton("GIOCA", skin);
        rootTable.add(playBtn).width(250).padBottom(15).row();

        // 2. Opzioni (Placeholder per il futuro)
        TextButton optionsBtn = new TextButton("OPZIONI", skin);
        rootTable.add(optionsBtn).width(250).padBottom(15).row();

        // 3. Logout
        TextButton logoutBtn = new TextButton("LOGOUT", skin);
        rootTable.add(logoutBtn).width(250).padBottom(15).row();

        // 4. Esci
        TextButton exitBtn = new TextButton("ESCI", skin);
        rootTable.add(exitBtn).width(250).row();

        // Setup listener locali (o delegati a metodo setupListeners)
        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.startGame();
            }
        });

        logoutBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.logout();
            }
        });

        exitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.exitGame();
            }
        });

        // Listener per Opzioni (vuoto per ora)
        optionsBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Opzioni non ancora implementate");
            }
        });
    }

    private void setupListeners() {
        // I listener sono stati definiti inline nel buildUI per brevità,
        // ma puoi spostarli qui per pulizia come nelle altre classi.
    }
}
