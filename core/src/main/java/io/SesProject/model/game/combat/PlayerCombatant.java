package io.SesProject.model.game.combat;

import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.Skill;
import io.SesProject.model.game.combat.skillsStrategy.SkillStrategy;

public class PlayerCombatant extends Combatant {

    private PlayerCharacter sourceData;

    public PlayerCombatant(PlayerCharacter pc) {
        super(pc.getName(), pc.getMaxHp() > 0 ? pc.getMaxHp() : 100);
        this.sourceData = pc;

        // SETUP ABILITÀ (Semplificato senza Factory esterna)
        initializeSkills(pc.getArchetype());
    }

    private void initializeSkills(String archetype) {
        // Behavior placeholder per testare (stampa in console e fa 10 danni)
        SkillStrategy dummyAttack = (user, target) -> {
            System.out.println(">> EFFETTO: Danno base applicato");
            target.takeDamage(10);
        };

        if (archetype.equalsIgnoreCase("Warrior")) { // Guardiano
            // 1. Fendente (Cooldown 0 - Sostituisce attacco base)
            this.skills.add(new Skill("Fendente", 0, dummyAttack));
            // 2. Provocazione
            this.skills.add(new Skill("Provocazione", 3, dummyAttack));
            // 3. Presa d'acciaio
            this.skills.add(new Skill("Presa d'Acciaio", 4, dummyAttack));
            // 4. Rottura Guardia
            this.skills.add(new Skill("Rottura Guardia", 3, dummyAttack));
            // 5. Ultimo Baluardo
            this.skills.add(new Skill("Ultimo Baluardo", 5, dummyAttack));
        }
        else if (archetype.equalsIgnoreCase("Mage")) { // Arcanista
            // 1. Dardo Arcano (Cooldown 0)
            this.skills.add(new Skill("Dardo Arcano", 0, dummyAttack));
            // 2. Congelamento
            this.skills.add(new Skill("Congelamento", 3, dummyAttack));
            // ... altre skill ...
            this.skills.add(new Skill("Supernova", 6, dummyAttack));
        }
    }


    @Override
    public String getSpriteName() {
        return this.sourceData.getArchetype();
    }
}
