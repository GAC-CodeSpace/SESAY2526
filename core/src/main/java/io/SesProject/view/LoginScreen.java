package io.SesProject.view;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.SesProject.controller.LoginController;

public class LoginScreen extends BaseMenuScreen {

    private LoginController controller;

    // Componenti UI
    private TextField userField;
    private TextButton loginBtn;
    private TextButton registerBtn;

    public LoginScreen(LoginController controller) {
        super(); // Chiama implicitamente buildUI()
        this.controller = controller;
        setupListeners();
    }

    @Override
    protected void buildUI() {
        rootTable.add(new Label("ACCEDI AL GIOCO", skin)).padBottom(30).row();

        userField = new TextField("", skin);
        userField.setMessageText("Nome del Profilo"); // Aggiornato
        rootTable.add(userField).width(300).padBottom(20).row();
        // Centriamo e diamo una dimensione
        rootTable.add(userField).width(300).height(40).padBottom(20).row();

        // Bottone Login
        loginBtn = new TextButton("LOGIN", skin);
        rootTable.add(loginBtn).width(200).height(50).padBottom(10).row();

        // Bottone Registrazione
        registerBtn = new TextButton("CREA NUOVO UTENTE", skin);
        rootTable.add(registerBtn).width(200).height(50).row();
    }

    private void setupListeners() {
        // Listener per il Login
        loginBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = userField.getText();
                // Deleghiamo la logica al controller
                controller.tryLogin(text);
            }
        });

        // Listener per la Registrazione
        registerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Deleghiamo la navigazione al controller
                controller.goToRegister();
            }
        });
    }
}
