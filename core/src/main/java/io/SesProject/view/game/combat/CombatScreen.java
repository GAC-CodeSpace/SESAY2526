package io.SesProject.view.game.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.SesProject.controller.CombatController;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.model.menu.MenuComposite;
import io.SesProject.view.BaseMenuScreen;

public class CombatScreen extends BaseMenuScreen {

    private CombatController controller;

    // Contenitore generico per le azioni (popolato dinamicamente)
    private Table actionsContainer;
    private Label turnLabel;

    // View degli osservatori (Label HP)
    private CombatantView p1InfoView;
    private CombatantView p2InfoView;
    private CombatantView enemyInfoView;

    public CombatScreen(CombatController controller) {
        super();
        this.controller = controller;
        // Ricorda: buildUI() è chiamato qui o nel padre a seconda della tua implementazione di BaseMenuScreen.
        // Se l'hai rimosso dal padre come suggerito in passato, chiamalo qui:
        buildUI();
    }

    @Override
    protected void buildUI() {
        // 1. Setup dei CombatantView (Observer)
        p1InfoView = new CombatantView(controller.getHeroes().get(0), skin);
        p2InfoView = new CombatantView(controller.getHeroes().get(1), skin);
        enemyInfoView = new CombatantView(controller.getEnemies().get(0), skin);

        // --- TOP: NEMICO ---
        Table topTable = new Table();
        topTable.top().setFillParent(true);

        // Sprite Placeholder Nemico (Rosso)
        Table enemySpriteBox = createSpritePlaceholder("Sprite Nemico", Color.RED);

        topTable.add(enemySpriteBox).size(128, 128).padBottom(10).row();
        topTable.add(enemyInfoView);

        // --- CENTER: EROI ---
        Table centerTable = new Table();
        centerTable.center().setFillParent(true);

        // Gruppo P1 (Info a sinistra, Sprite a destra)
        Table p1Group = new Table();
        p1Group.add(p1InfoView).padRight(10);
        p1Group.add(createSpritePlaceholder("P1 Sprite", Color.BLUE)).size(96, 96);

        // Gruppo P2 (Sprite a sinistra, Info a destra)
        Table p2Group = new Table();
        p2Group.add(createSpritePlaceholder("P2 Sprite", Color.CYAN)).size(96, 96).padRight(10);
        p2Group.add(p2InfoView);

        centerTable.add(p1Group).padRight(50);
        centerTable.add(p2Group).padLeft(50);

        // --- BOTTOM: MENU DINAMICO ---
        Table bottomTable = new Table();
        bottomTable.bottom().setFillParent(true);

        turnLabel = new Label("", skin);
        turnLabel.setColor(Color.YELLOW);
        bottomTable.add(turnLabel).padBottom(10).row();

        // Contenitore per le categorie (Attacchi, Abilità, ecc.)
        actionsContainer = new Table();
        bottomTable.add(actionsContainer).growX().pad(10);

        // Assemblaggio Stage
        stage.addActor(topTable);
        stage.addActor(centerTable);
        stage.addActor(bottomTable);

        // Popolamento iniziale
        updateActionMenu();
    }

    /**
     * Metodo helper per creare i box segnaposto degli sprite.
     */
    private Table createSpritePlaceholder(String text, Color color) {
        Table t = new Table();
        // Nota: Assicurati di avere una texture 'white' nella skin o usa un debug background
        if (skin.has("white", com.badlogic.gdx.scenes.scene2d.utils.Drawable.class)) {
            t.setBackground(skin.newDrawable("white", color));
        } else {
            // Fallback se non hai la texture bianca: usa il debug lines
            t.debug();
        }
        t.add(new Label(text, skin));
        return t;
    }

    /**
     * Chiamato dal Controller quando cambia il turno o dopo un'azione.
     * Ricostruisce i pannelli usando il Pattern Composite e gestisce visualmente i Cooldown.
     */
    public void updateActionMenu() {
        turnLabel.setText("Turno di: " + controller.getCurrentActor().getName());
        actionsContainer.clear(); // Pulisce i bottoni del turno precedente

        if (controller.isPlayerTurn()) {
            MenuComponent menuRoot = controller.getDynamicCombatMenu();

            // Iteriamo sui figli della radice (che sono le CATEGORIE: Azioni, ecc...)
            for (MenuComponent category : menuRoot.getChildren()) {

                // Per ogni categoria, creiamo un pannello visivo (colonna)
                Table categoryTable = new Table();

                // 1. Intestazione Categoria
                Label header = new Label(category.getName(), skin);
                header.setColor(Color.ORANGE);
                categoryTable.add(header).padBottom(5).row();

                // 2. Contenitore scrollabile per le voci
                Table itemsTable = new Table();
                ScrollPane scroll = new ScrollPane(itemsTable, skin);
                scroll.setFadeScrollBars(false);

                // 3. Iteriamo sui figli della Categoria (le Foglie/Azioni)
                if (!category.getChildren().isEmpty()) {
                    for (MenuComponent actionItem : category.getChildren()) {

                        TextButton btn = new TextButton(actionItem.getName(), skin);

                        // --- LOGICA VISIVA COOLDOWN ---
                        // Il controller formatta il nome come "NomeSkill (Cooldown)" se non è pronta.
                        // Usiamo questo dettaglio per disabilitare il bottone.
                        if (actionItem.getName().contains("(")) {
                            // Bottone Disabilitato (In Cooldown)
                            btn.setDisabled(true);
                            btn.setColor(Color.GRAY); // Grigio scuro per indicare inattività
                            // Non aggiungiamo listener: il click non farà nulla
                        } else {
                            // Bottone Attivo
                            btn.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    // 1. Esegue il Command (UseSkillCommand)
                                    actionItem.select();

                                    // 2. Aggiorna immediatamente la UI
                                    // (così il bottone appena premuto diventa grigio col cooldown)
                                    updateActionMenu();
                                }
                            });
                        }

                        itemsTable.add(btn).growX().pad(2).row();
                    }
                }

                // Aggiungiamo il pannello della categoria al container principale
                categoryTable.add(scroll).width(200).height(150);
                actionsContainer.add(categoryTable).expandX().top();
            }
        } else {
            // Turno nemico
            actionsContainer.add(new Label("Turno avversario in corso...", skin));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (p1InfoView != null) p1InfoView.detach();
        if (p2InfoView != null) p2InfoView.detach();
        if (enemyInfoView != null) enemyInfoView.detach();
    }
}

