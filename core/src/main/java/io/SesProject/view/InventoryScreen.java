package io.SesProject.view;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.SesProject.controller.InventoryController;
import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.item.observer.PlayerStatsObserver;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.model.menu.MenuComposite;

//CONCRETE OBSERVER
public class InventoryScreen extends BaseMenuScreen implements PlayerStatsObserver {

    private InventoryController controller;
    private Table p1Panel, p2Panel;



    public InventoryScreen(InventoryController controller) {
        super();
        this.controller = controller;
        buildUI();
    }

    @Override
    protected void buildUI() {
        rootTable.add(new Label("INVENTARIO", skin)).colspan(2).padBottom(20).row();

        p1Panel = new Table();
        p2Panel = new Table();

        // Layout Split Screen
        rootTable.add(p1Panel).expand().fill().padRight(10);
        rootTable.add(p2Panel).expand().fill().padLeft(10).row();

        // Back Button
        TextButton backBtn = new TextButton("INDIETRO", skin);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                controller.backToPause();
            }
        });
        rootTable.add(backBtn).colspan(2).padTop(20);

        setupObserversAndPanels();
    }

    private void setupObserversAndPanels() {
        PlayerCharacter p1 = controller.getP1();
        PlayerCharacter p2 = controller.getP2();

        // 2. REGISTRAZIONE DI 'THIS' COME OSSERVATORE
        // La View stessa è l'osservatore.
        p1.addObserver(this);
        p2.addObserver(this);

        // Costruzione iniziale
        refreshPlayerColumn(p1Panel, p1, "PLAYER 1");
        refreshPlayerColumn(p2Panel, p2, "PLAYER 2");
    }


    /**
     * Ricostruisce la colonna di un giocatore usando il COMPOSITE PATTERN.
     */
    private void refreshPlayerColumn(Table panel, PlayerCharacter pc, String title) {
        panel.clear();
        panel.top();

        // 1. Header (Nome e Archetipo)
        Label titleLabel = new Label(title + ": " + pc.getArchetype(), skin);
        titleLabel.setColor(Color.YELLOW); // Un tocco di colore per il titolo
        panel.add(titleLabel).padBottom(5).row();

        // 2. Level Display
        String levelStr = "Livello: " + pc.getLevel();
        Label levelLabel = new Label(levelStr, skin);
        levelLabel.setColor(Color.GOLD);
        panel.add(levelLabel).padBottom(2).row();

        // 3. XP Progress
        String xpStr = "XP: " + pc.getExperience() + " / " + pc.getXpForNextLevel();
        Label xpLabel = new Label(xpStr, skin);
        xpLabel.setColor(Color.CYAN);
        panel.add(xpLabel).padBottom(2).row();

        // 4. Stats - Riga 1: HP
        // Usiamo un colore diverso se la vita è bassa (opzionale ma carino)
        String hpStr = "HP: " + pc.getHp() + "/" + pc.getMaxHp();
        Label hpLabel = new Label(hpStr, skin);
        if (pc.getHp() < pc.getMaxHp() * 0.3f) hpLabel.setColor(Color.RED);
        panel.add(hpLabel).padBottom(2).row();

        // 3. Stats - Riga 2: ATK e KARMA (--- NUOVO ---)
        // Li mettiamo sulla stessa riga per risparmiare spazio verticale
        String statsStr = "ATK: " + pc.getAttackPower() + "  |  KARMA: " + pc.getKarma();
        Label statsLabel = new Label(statsStr, skin);

        // Feedback visivo sul Karma: Rosso se negativo, Verde se positivo, Bianco se neutro
        if (pc.getKarma() < 0) {
            statsLabel.setColor(new Color(1f, 0.4f, 0.4f, 1f)); // Rosso chiaro
        } else if (pc.getKarma() > 10) { // Soglia esempio per karma "buono"
            statsLabel.setColor(new Color(0.4f, 1f, 0.4f, 1f)); // Verde chiaro
        }

        panel.add(statsLabel).padBottom(15).row();

        // 4. Rendering del Menu Composite (Invariato)
        MenuComponent menuRoot = controller.getInventoryTree(pc);
        renderCompositeTree(panel, menuRoot);
    }

    /**
     * Metodo ricorsivo (o iterativo) per disegnare l'albero Composite.
     */
    private void renderCompositeTree(Table container, MenuComponent component) {
        // Se è un nodo composto (Categoria)
        if (component instanceof MenuComposite) {

            // Se non è la root (che è invisibile), disegna l'header
            if (!component.getName().equals("ROOT")) {
                Label header = new Label(component.getName(), skin);
                header.setColor(Color.ORANGE);
                container.add(header).padTop(10).padBottom(5).row();
            }

            // Crea un sottocontenitore (opzionale, per indentazione o scroll)
            Table subTable = new Table();

            // Itera sui figli
            if (component.getChildren() != null) {
                for (MenuComponent child : component.getChildren()) {
                    renderCompositeTree(subTable, child); // Ricorsione
                }
            }

            // Se è la sezione Zaino, mettiamola in uno ScrollPane per non sforare
            if (component.getName().equals("ZAINO")) {
                ScrollPane scroll = new ScrollPane(subTable, skin);
                scroll.setFadeScrollBars(false);
                container.add(scroll).grow().height(250).row();
            } else {
                container.add(subTable).growX().row();
            }
        }
        // Se è una foglia (MenuItem/Bottone)
        else {
            TextButton btn = new TextButton(component.getName(), skin);
            btn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    component.select();
                    // Nota: Non serve chiamare refreshUI qui manualmente,
                    // perché il comando modificherà il Model, che notificherà l'Observer,
                    // che chiamerà refreshPlayerColumn.
                }
            });

            container.add(btn).growX().pad(2).row();
        }
    }

    @Override
    public void onStatsChanged(PlayerCharacter subject) {
        // Poiché osserviamo due soggetti, dobbiamo capire chi è cambiato
        // per aggiornare solo la colonna giusta.

        if (subject == controller.getP1()) {
            refreshPlayerColumn(p1Panel, subject, "PLAYER 1");
        }
        else if (subject == controller.getP2()) {
            refreshPlayerColumn(p2Panel, subject, "PLAYER 2");
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        // 4. RIMUOVIAMO 'THIS' DAI SOGGETTI
        if (controller.getP1() != null) controller.getP1().removeObserver(this);
        if (controller.getP2() != null) controller.getP2().removeObserver(this);
    }

    public void updateMenuRoot(MenuComponent newRoot, PlayerCharacter pc) {
        // Identifichiamo quale pannello deve mostrare la scelta delle skill
        Table targetPanel = (pc == controller.getP1()) ? p1Panel : p2Panel;
        String title = (pc == controller.getP1()) ? "PLAYER 1" : "PLAYER 2";

        // Puliamo il pannello e mostriamo solo il titolo e il nuovo menu di scelta
        targetPanel.clear();
        targetPanel.top();
        targetPanel.add(new Label(title + " - SELEZIONE POTENZIAMENTO", skin)).padBottom(20).row();

        // Disegniamo il menu di scelta (le skill) nel pannello
        renderCompositeTree(targetPanel, newRoot);
    }
}

