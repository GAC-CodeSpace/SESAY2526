package io.SesProject.view.game.combat;

import io.SesProject.model.game.combat.Combatant;

public class StatusEffect {
    private String type;     // Esempio: "STUN", "TAUNT", "BUFF"
    private int duration;    // Durata in turni
    private float modifier;  // Valore numerico dell'effetto (es. 1.5f per +50% danno)
    private Combatant source; // Chi ha lanciato l'effetto (utile per Taunt)

    // Costruttore base (es. per Stun)
    public StatusEffect(String type, int duration) {
        this(type, duration, 1.0f, null);
    }

    // Costruttore completo (es. per Buff o Taunt)
    public StatusEffect(String type, int duration, float modifier) {
        this(type, duration, modifier, null);
    }

    public StatusEffect(String type, int duration, Combatant source) {
        this(type, duration, 1.0f, source);
    }

    public StatusEffect(String type, int duration, float modifier, Combatant source) {
        this.type = type;
        this.duration = duration;
        this.modifier = modifier;
        this.source = source;
    }

    public void tick() {
        if (duration > 0) duration--;
    }

    public void setExpired(boolean expired) {
        if (expired) {
            this.duration = 0;
        }
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    public String getType() { return type; }
    public float getModifier() { return modifier; }
    public Combatant getSource() { return source; }
}
