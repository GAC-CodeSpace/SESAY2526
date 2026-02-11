package io.SesProject.model.game.combat;

import io.SesProject.model.game.Skill;
import io.SesProject.model.game.combat.combatObs.CombatantObserver;
import io.SesProject.view.game.combat.StatusEffect;

import java.util.ArrayList;
import java.util.List;
/*SUBJECT dell'observer*/


public abstract class Combatant {
    protected String name;
    protected int currentHp;
    protected int maxHp;
    protected int attackPower;

    protected List<StatusEffect> activeEffects = new ArrayList<>();

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
        int finalDamage = dmg;

        // Gestione degli effetti attivi prima di applicare il danno
        for (StatusEffect e : new ArrayList<>(activeEffects)) {
            // Scudo Magico (Mago): riduce il danno del 50% e riflette il 20%
            if (e.getType().equals("SHIELD_REFLECT")) {
                finalDamage = (int) (dmg * e.getModifier()); // Il mago subisce il 50%

                if (e.getSource() != null) {
                    // Riflette il 20% del danno ORIGINALE al nemico
                    e.getSource().takeDamage((int) (dmg * 0.2f));
                    System.out.println("Scudo riflette danno a " + e.getSource().getName());
                }
                e.setExpired(true);
            }
            // Rottura Guardia (Tank): il nemico subisce il 50% di danni in più
            else if (e.getType().equals("VULNERABLE")) {
                finalDamage = (int) (dmg * e.getModifier()); // modifier è 1.5f
                e.setExpired(true); // L'effetto si consuma
            }
        }

        this.currentHp -= finalDamage;
        if (this.currentHp < 0) this.currentHp = 0;

        notifyHealthChanged();
        if (this.currentHp == 0) notifyDeath();
    }

    public void heal(int amount) {
        this.currentHp = Math.min(maxHp, currentHp + amount);
        notifyHealthChanged(); // Notifica la UI della cura
    }

    public void addStatusEffect(StatusEffect effect) {
        this.activeEffects.add(effect);
    }


    public void setMaxHp(int newMax) {
        this.maxHp = newMax;
    }

    // Da chiamare in tickCooldowns() per diminuire la durata dei buff/debuff
    public void updateEffects() {
        activeEffects.removeIf(e -> e.isExpired());
        for (StatusEffect e : activeEffects) e.tick();
    }

    public List<StatusEffect> getActiveEffects() {
        return activeEffects;
    }

    // Metodo per verificare se è stordito (per il Mago)
    public boolean isStunned() {
        return activeEffects.stream().anyMatch(e -> e.getType().equals("STUN"));
    }


    public void setAttackPower(int power) {
        this.attackPower = power;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public abstract String getSpriteName();

    public String getName() {
        return name;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public String getDisplayName() {
        return name;
    }

    public List<Skill> getSkills() {
        return skills;
    }
}
