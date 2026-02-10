package io.SesProject.model.game.combat;

import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.Skill;
import io.SesProject.model.game.combat.skillsStrategy.SkillStrategy;
import io.SesProject.view.game.combat.StatusEffect;

public class PlayerCombatant extends Combatant {

    private PlayerCharacter sourceData;

    public PlayerCombatant(PlayerCharacter pc) {
        super(pc.getName(), pc.getMaxHp() > 0 ? pc.getMaxHp() : 100);
        this.sourceData = pc;
        // IMPORTANTE: Prendi la forza d'attacco dai dati del personaggio
        this.setAttackPower(pc.getAttackPower());
        initializeSkills(pc.getArchetype());
    }

    private void initializeSkills(String archetype) {
        if (archetype.equalsIgnoreCase("Warrior")) {
            // 1. Fendente: Danno base
            this.skills.add(new Skill("Fendente", 0, (u, t, ctx) -> t.takeDamage(u.getAttackPower())));

            // 2. Provocazione: Forza il nemico ad attaccare il tank per 2 turni
            this.skills.add(new Skill("Provocazione", 3, (u, t, ctx) -> t.addStatusEffect(new StatusEffect("TAUNT", 2, u))));

            // 3. Presa d'acciaio: Aumenta HP max di entrambi
            this.skills.add(new Skill("Presa d'Acciaio", 4, (u, t, ctx) -> {
                ctx.stream().filter(c -> c instanceof PlayerCombatant).forEach(c -> {
                    c.setMaxHp(c.getMaxHp() + 12); // Aumenta il limite massimo
                    c.heal(12); // Cura i nuovi HP aggiunti
                });
            }));

            // 4. Rottura Guardia: Prossimo attacco su nemico +50%
            this.skills.add(new Skill("Rottura Guardia", 3, (u, t, ctx) -> t.addStatusEffect(new StatusEffect("VULNERABLE", 1, 1.5f))));

// 5. Ultimo Baluardo (Fix: Forza il calcolo dei danni)
            this.skills.add(new Skill("Ultimo Baluardo", 5, (u, t, ctx) -> {
                int dmg = Math.max(u.getAttackPower() * 4, 40); // Almeno 40 danni garantiti
                t.takeDamage(dmg);
                u.takeDamage((int) (u.getCurrentHp() * 0.2f));
            }));
        } else if (archetype.equalsIgnoreCase("Mage")) {
            // 1. Dardo Arcano: Danno base
            this.skills.add(new Skill("Dardo Arcano", 0, (u, t, ctx) -> t.takeDamage(u.getAttackPower())));

            // 2. Congelamento: Danno basso + Salta turno (STUN)
            this.skills.add(new Skill("Congelamento", 3, (u, t, ctx) -> {
                t.takeDamage(5);
                t.addStatusEffect(new StatusEffect("STUN", 1));
            }));

// 3. Tributo di Sangue (Fix: Aggiunto danno reale)
            this.skills.add(new Skill("Tributo di Sangue", 4, (u, t, ctx) -> {
                int dmg = u.getAttackPower() * 2;
                t.takeDamage(dmg);
                ctx.stream().filter(c -> c != u && c instanceof PlayerCombatant).forEach(c -> c.heal(15));
            }));

            // 4. Scudo Magico: Riduce danno ricevuto e riflette
            this.skills.add(new Skill("Scudo Magico", 4, (u, t, ctx) ->
                u.addStatusEffect(new StatusEffect("SHIELD_REFLECT", 1, 0.5f, t))
            ));
// 5. Supernova (Fix: Danno ad area funzionante)
            this.skills.add(new Skill("Supernova", 6, (u, t, ctx) -> {
                int mainDmg = u.getAttackPower() * 5;
                t.takeDamage(mainDmg); // Danno enorme al nemico
                ctx.forEach(c -> {
                    if (c != t) c.takeDamage(10); // Danno fisso leggero ai giocatori
                });
            }));
        }
    }


    @Override
    public String getSpriteName() {
        return this.sourceData.getArchetype();
    }

    public String getArchetype() {
        return this.sourceData.getArchetype();
    }
}
