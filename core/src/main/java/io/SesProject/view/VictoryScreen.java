package io.SesProject.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.SesProject.controller.VictoryController;
import io.SesProject.model.game.combat.CombatReward;

/**
 * Victory Screen displayed after winning a combat.
 * Shows XP gained, Karma gained, and any level ups.
 */
public class VictoryScreen extends BaseMenuScreen {

    private VictoryController controller;

    public VictoryScreen(VictoryController controller) {
        super();
        this.controller = controller;
        buildUI();
    }

    @Override
    protected void buildUI() {
        CombatReward reward = controller.getReward();

        // Title - using default style with larger font scale
        Label titleLabel = new Label("VITTORIA!", skin);
        titleLabel.setFontScale(2.0f); // Make it bigger
        titleLabel.setColor(Color.GOLD);
        rootTable.add(titleLabel).padBottom(30).row();

        // Rewards section
        Label rewardsTitle = new Label("Ricompense Ottenute:", skin);
        rewardsTitle.setColor(Color.YELLOW);
        rootTable.add(rewardsTitle).padBottom(15).row();

        // XP Gained
        Label xpLabel = new Label("Esperienza: +" + reward.getTotalXp() + " XP", skin);
        xpLabel.setColor(Color.CYAN);
        rootTable.add(xpLabel).padBottom(10).row();

        // Karma Gained
        Label karmaLabel = new Label("Karma: +" + reward.getTotalKarma(), skin);
        karmaLabel.setColor(Color.GREEN);
        rootTable.add(karmaLabel).padBottom(20).row();

        // Level Ups (if any)
        if (reward.hasLevelUps()) {
            Label levelUpTitle = new Label("LEVEL UP!", skin);
            levelUpTitle.setFontScale(1.5f); // Larger font
            levelUpTitle.setColor(Color.ORANGE);
            rootTable.add(levelUpTitle).padBottom(10).row();

            for (String playerName : reward.getLevelUps()) {
                Label levelUpLabel = new Label(playerName + " ha raggiunto un nuovo livello!", skin);
                levelUpLabel.setColor(Color.ORANGE);
                rootTable.add(levelUpLabel).padBottom(5).row();
            }

            rootTable.row().padBottom(20);
        }

        // Continue Button
        TextButton continueBtn = new TextButton("CONTINUA", skin);
        continueBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.continueToGame();
            }
        });
        rootTable.add(continueBtn).width(200).height(50).padTop(20);
    }
}


