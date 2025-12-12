package io.SesProject.view;



import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.SesProject.controller.RegisterController;


public class RegisterScreen extends BaseMenuScreen {

    private RegisterController controller;

    private TextField userField;
    private TextButton confirmBtn;
    private TextButton backBtn;

    public RegisterScreen(RegisterController controller) {
        super();
        this.controller = controller;
        setupListeners();
    }

    @Override
    protected void buildUI() {
        rootTable.add(new Label("CREA NUOVO EROE", skin)).padBottom(30).row();

        // Campo input
        userField = new TextField("", skin);
        userField.setMessageText("Scegli un nome univoco");
        rootTable.add(userField).width(300).padBottom(20).row();

        // Bottone Conferma
        confirmBtn = new TextButton("REGISTRA", skin);
        rootTable.add(confirmBtn).width(200).padBottom(10).row();

        // Bottone Indietro
        backBtn = new TextButton("TORNA AL LOGIN", skin);
        rootTable.add(backBtn).width(200).row();
    }

    private void setupListeners() {
        confirmBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.tryRegister(userField.getText());
            }
        });

        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.backToLogin();
            }
        });
    }
}
