package io.SesProject.view;



import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.SesProject.controller.LoadGameController;
import io.SesProject.model.SaveMetadata;

import java.util.List;

public class LoadGameScreen extends BaseMenuScreen {

    private LoadGameController controller;

    public LoadGameScreen(LoadGameController controller) {
        super();
        this.controller = controller;
        rootTable.clear(); // Pulizia necessaria per la gestione layout custom
        buildUI();
    }

    @Override
    protected void buildUI() {
        // Titolo
        rootTable.add(new Label("CARICA PARTITA", skin)).padBottom(20).row();

        // Recuperiamo i dati dal Controller
        List<SaveMetadata> saves = controller.getSaveList();

        if (saves.isEmpty()) {
            rootTable.add(new Label("Nessun salvataggio trovato.", skin)).padBottom(20).row();
        } else {
            // Creiamo una tabella interna per la lista scorrevole
            Table listTable = new Table();

            for (SaveMetadata meta : saves) {
                // Testo del bottone: "Slot 1 - 2023-01-01 - Lvl 1"
                String btnText = meta.toString();
                TextButton slotBtn = new TextButton(btnText, skin);

                // Listener: Chiama loadSlot(id)
                slotBtn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        controller.loadSlot(meta.getSlotId());
                    }
                });

                listTable.add(slotBtn).width(400).height(50).padBottom(10).row();
            }

            // Mettiamo la tabella in uno ScrollPane (caso ci siano tanti salvataggi)
            ScrollPane scrollPane = new ScrollPane(listTable, skin);
            rootTable.add(scrollPane).width(450).height(300).padBottom(20).row();
        }

        // Bottone Indietro
        TextButton backBtn = new TextButton("INDIETRO", skin);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.backToMain();
            }
        });
        rootTable.add(backBtn).width(200);
    }
}
