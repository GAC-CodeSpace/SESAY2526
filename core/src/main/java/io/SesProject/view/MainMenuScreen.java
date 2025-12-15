package io.SesProject.view;



import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.SesProject.controller.MainMenuController;
import io.SesProject.model.menu.MenuComponent;


public class MainMenuScreen extends BaseMenuScreen {

    @SuppressWarnings("unused")
    private MainMenuController controller;
    private MenuComponent menuRoot;

    /**
     * @param controller Il controller che gestisce questa view
     * @param menuRoot La radice dell'albero Composite da disegnare
     */
    public MainMenuScreen(MainMenuController controller, MenuComponent menuRoot) {
        super();
        this.controller = controller;
        this.menuRoot = menuRoot;

        // Ricostruiamo la UI ora che abbiamo i dati del menu
        rootTable.clear();
        buildUI();
    }

    @Override
    protected void buildUI() {
        // Controllo di sicurezza
        if (menuRoot == null) return;

        // 1. Titolo del Menu
        Label titleLabel = new Label(menuRoot.getName(), skin);
        titleLabel.setFontScale(2.0f); // Titolo più grande
        rootTable.add(titleLabel).padBottom(50).row();

        // 2. Iterazione sui figli (Composite Pattern)
        for (MenuComponent child : menuRoot.getChildren()) {

            // Creiamo un bottone per ogni voce del menu
            TextButton btn = new TextButton(child.getName(), skin);

            // Il listener invoca il metodo select() del componente (che esegue il Command)
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    child.select();
                }
            });

            // Aggiungiamo alla tabella UI
            rootTable.add(btn).width(300).height(60).padBottom(20).row();
        }
    }
}
