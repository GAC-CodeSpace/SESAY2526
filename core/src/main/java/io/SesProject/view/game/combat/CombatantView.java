package io.SesProject.view.game.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.SesProject.model.game.combat.Combatant;
import io.SesProject.model.game.combat.combatObs.CombatantObserver;


/*CONCRETE OBSERVER del pattern observer*/
public class CombatantView extends Table implements CombatantObserver {
    private Label nameLabel;
    private Label hpLabel;
    private Combatant subject;

    public CombatantView(Combatant subject, Skin skin) {
        super(skin);
        this.subject = subject;

        this.nameLabel = new Label(subject.getName(), skin);
        this.hpLabel = new Label(formatHpString(), skin);

        this.add(nameLabel).padBottom(5).row();
        this.add(hpLabel).row();

        // Registrazione Observer
        this.subject.addObserver(this);
    }

    @Override
    public void onHealthChanged(Combatant source) {
        hpLabel.setText(formatHpString());
        hpLabel.setColor(Color.RED);
        hpLabel.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.color(Color.WHITE, 0.5f));
    }

    @Override
    public void onDeath(Combatant source) {
        hpLabel.setText("SCONFITTO");
        hpLabel.setColor(Color.GRAY);
        nameLabel.setColor(Color.GRAY);
    }

    private String formatHpString() {
        return "HP: " + subject.getCurrentHp() + " / " + subject.getMaxHp();
    }

    public void detach() {
        if (subject != null) {
            subject.removeObserver(this);
        }
    }
}
