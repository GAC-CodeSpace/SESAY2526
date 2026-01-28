package io.SesProject.model.game.combat;

import io.SesProject.model.game.Skill;
import io.SesProject.model.game.combat.combatObs.CombatantObserver;

import java.util.ArrayList;
import java.util.List;
/*SUBJECT dell'observer*/


public abstract class Combatant {
    protected String name;
    protected int currentHp;
    protected int maxHp;

    //Lista di skill possedute
    protected List<Skill> skills;

    // Lista degli osservatori
    private List<CombatantObserver> observers;

    public Combatant(String name, int maxHp) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.observers = new ArrayList<>();

        this.skills = new ArrayList<>();
    }

    public void addObserver(CombatantObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(CombatantObserver observer) {
        observers.remove(observer);
    }

    private void notifyHealthChanged() {
        for (CombatantObserver o : observers) {
            o.onHealthChanged(this);
        }
    }

    private void notifyDeath() {
        for (CombatantObserver o : observers) {
            o.onDeath(this);
        }
    }

    // Chiamato dal Controller a inizio turno
    public void tickCooldowns() {
        for (Skill s : skills) {
            s.reduceCooldown();
        }
    }

    public void takeDamage(int dmg) {
        this.currentHp -= dmg;
        if (this.currentHp < 0) this.currentHp = 0;

        notifyHealthChanged();

        if (this.currentHp == 0) {
            notifyDeath();
        }
    }


    public String getName() { return name; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public List<Skill> getSkills() { return skills; }
}
