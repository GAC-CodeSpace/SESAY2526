package io.SesProject.view.game.combat;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import io.SesProject.controller.CombatController;
import io.SesProject.model.game.combat.Combatant;
import io.SesProject.model.game.visualState.VisualState;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.game.flyweightFactory.SpriteFlyweightFactory;

public class CombatScreen extends BaseMenuScreen {

    private CombatController controller;

    // Contenitore generico per le azioni (popolato dinamicamente)
    private Table actionsContainer;
    private Label turnLabel;

    // View degli osservatori (Label HP)
    private CombatantView p1InfoView;
    private CombatantView p2InfoView;
    private CombatantView enemyInfoView;

    private SpriteFlyweightFactory spriteFactory;

    public CombatScreen(CombatController controller) {
        super();
        this.controller = controller;

        // Recuperiamo il Facade dal gioco tramite il controller
        // Assicurati che GameController o BaseController esponga getGame()
        this.spriteFactory = new SpriteFlyweightFactory(controller.getGame().getSystemFacade());

        buildUI();
    }

    @Override
    protected void buildUI() {
        // Recuperiamo i combattenti dal controller
        Combatant p1 = controller.getHeroes().get(0);
        Combatant p2 = controller.getHeroes().get(1);
        Combatant enemy = controller.getEnemies().get(0);

        // 1. Setup dei CombatantView (Observer per HP/Nome)
        p1InfoView = new CombatantView(p1, skin);
        p2InfoView = new CombatantView(p2, skin);
        enemyInfoView = new CombatantView(enemy, skin);

        // --- TOP: NEMICO ---
        Table topTable = new Table();
        topTable.top().setFillParent(true);

        // Recupera Sprite Reale Nemico
        Image enemyImage = createCombatantImage(enemy);

        // Lo mostriamo grande (128x128) perché è il nemico principale
        topTable.add(enemyImage).size(128, 128).padBottom(10).row();
        topTable.add(enemyInfoView);

        // --- CENTER: EROI ---
        Table centerTable = new Table();
        centerTable.center().setFillParent(true);

        // Gruppo P1 (Info a sinistra, Sprite a destra)
        Table p1Group = new Table();
        Image p1Image = createCombatantImage(p1);

        p1Group.add(p1InfoView).padRight(10);
        p1Group.add(p1Image).size(96, 96); // Sprite P1

        // Gruppo P2 (Sprite a sinistra, Info a destra)
        Table p2Group = new Table();
        Image p2Image = createCombatantImage(p2);

        p2Group.add(p2Image).size(96, 96).padRight(10); // Sprite P2
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

        // Popolamento iniziale menu
        updateActionMenu();
    }

    /**
     * NUOVO METODO: Crea un attore Image recuperando la texture dalla Factory.wwwwwwwww
     */
    private Image createCombatantImage(Combatant c) {
        // Recupera il nome dello sprite (es. "warrior", "goblin") dal Model
        String spriteName = c.getSpriteName();

        // Per il menu di combattimento, usiamo lo stato IDLE_DOWN (faccia avanti)
        VisualState state = VisualState.IDLE_DOWN;

        // Chiede alla Flyweight Factory l'animazione corretta dall'Atlas
        Animation<TextureRegion> anim = spriteFactory.getAnimation(spriteName, state);

        if (anim != null) {
            // Prende il primo frame dell'animazione (statico)
            TextureRegion region = anim.getKeyFrame(0);

            // Crea l'Image Scene2D
            Image img = new Image(new TextureRegionDrawable(region));

            // Scaling.fit assicura che l'immagine non venga deformata se cambiamo size() nella tabella
            img.setScaling(Scaling.fit);
            return img;
        } else {
            // Fallback se l'immagine non esiste nell'Atlas: ritorna un'immagine vuota (o di debug)
            System.err.println("[UI] Immagine non trovata per: " + spriteName);
            return new Image();
        }
    }

    /**
     * Chiamato dal Controller quando cambia il turno o dopo un'azione.
     * Ricostruisce i pannelli usando il Pattern Composite.
     */
    public void updateActionMenu() {
        turnLabel.setText("Turno di: " + controller.getCurrentActor().getDisplayName());
        actionsContainer.clear(); // Pulisce i bottoni del turno precedente

        if (controller.isPlayerTurn()) {
            MenuComponent menuRoot = controller.getDynamicCombatMenu();

            // Iteriamo sui figli della radice (Categorie)
            for (MenuComponent category : menuRoot.getChildren()) {

                Table categoryTable = new Table();

                // Intestazione Categoria
                Label header = new Label(category.getName(), skin);
                header.setColor(Color.ORANGE);
                categoryTable.add(header).padBottom(5).row();

                // ScrollPane per le voci
                Table itemsTable = new Table();
                ScrollPane scroll = new ScrollPane(itemsTable, skin);
                scroll.setFadeScrollBars(false);

                // Iteriamo sui figli della Categoria (Azioni)
                if (!category.getChildren().isEmpty()) {
                    for (MenuComponent actionItem : category.getChildren()) {

                        TextButton btn = new TextButton(actionItem.getName(), skin);

                        // Gestione Cooldown Visiva
                        if (actionItem.getName().contains("(")) {
                            btn.setDisabled(true);
                            btn.setColor(Color.GRAY);
                        } else {
                            btn.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    actionItem.select(); // Esegue il Command
                                    updateActionMenu();  // Refresh UI immediato
                                }
                            });
                        }
                        itemsTable.add(btn).growX().pad(2).row();
                    }
                }

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
        super.dispose(); // Chiama il dispose dello Stage

        System.out.println("[UI] Distruzione CombatScreen: stacco gli observers...");

        // Stacca gli observer dai rispettivi subject per evitare memory leak
        // e notifiche fantasma.
        if (p1InfoView != null) {
            p1InfoView.detach();
        }
        if (p2InfoView != null) {
            p2InfoView.detach();
        }
        if (enemyInfoView != null) {
            enemyInfoView.detach();
        }
    }
}
