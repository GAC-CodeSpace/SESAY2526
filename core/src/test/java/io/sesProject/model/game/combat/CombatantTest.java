package io.sesProject.model.game.combat;

import io.SesProject.model.game.combat.Combatant;
import io.SesProject.view.game.combat.StatusEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CombatantTest {

    // Concrete implementation for testing abstract Combatant
    private static class TestCombatant extends Combatant {
        public TestCombatant(String name, int maxHp) {
            super(name, maxHp);
        }

        @Override
        public String getSpriteName() {
            return "test_sprite";
        }
    }

    private TestCombatant combatant;
    private TestCombatant enemy;

    @BeforeEach
    void setUp() {
        combatant = new TestCombatant("Hero", 100);
        enemy = new TestCombatant("Monster", 100);
    }

    @Test
    void testTakeDamage() {
        combatant.takeDamage(10);
        assertEquals(90, combatant.getCurrentHp());

        combatant.takeDamage(95); // Overkill
        assertEquals(0, combatant.getCurrentHp()); // Should not be negative
    }

    @Test
    void testHeal() {
        combatant.takeDamage(50);
        assertEquals(50, combatant.getCurrentHp());

        combatant.heal(20);
        assertEquals(70, combatant.getCurrentHp());

        combatant.heal(100); // Overheal
        assertEquals(100, combatant.getCurrentHp()); // Capped at maxHp
    }

    @Test
    void testShieldReflect() {
        // "SHIELD_REFLECT": reduces damage by 50% and reflects 20% to source
        StatusEffect shield = new StatusEffect("SHIELD_REFLECT", 1, 0.5f, combatant);
        // Note: Logic in Combatant:
        // if (e.getSource() != null) { e.getSource().takeDamage(...) }
        // Wait. In Combatant.java:76: e.getSource().takeDamage(...)
        // Usually source of a buff/shield is the caster (self).
        // But reflection logic says: "Riflette il 20% del danno ORIGINALE al nemico"
        // If I cast Shield on myself, source is ME. If I take damage, it should reflect to the ATTACKER.
        // But Combatant.java code uses e.getSource().
        // Line 74: if (e.getSource() != null)
        // Line 76: e.getSource().takeDamage(...)
        // This implies e.getSource() is the one receiving the reflected damage.
        // So if I am the target, the source of the effect (SHIELD) should be the one who cast it?
        // Wait, if I cast shield on myself, source is me. So I take the reflected damage? That seems wrong.
        // The logic seems to assume that the StatusEffect holds reference to the "Attacker" or something?
        // "Scudo riflette danno a " + e.getSource().getName()
        // If it's a shield I cast on myself to reflect damage back to attacker, I can't know the attacker from the StatusEffect unless the status effect is applied BY the attacker? No.

        // Let's assume the logic in Combatant.java is:
        // Shield is applied to ME.
        // If I take damage, I reflect to... whom?
        // e.getSource() is the combatant stored in the effect.

        // If the intention is "Reflect to attacker", then logic is flawed because takeDamage(int dmg) doesn't know the source of damage.
        // UNLESS... the status effect's source IS the one who will receive the reflection?
        // But usually Shield is a buff I put on myself.

        // Let's test what the code DOES, not what it SHOULD do (unless I fix it).
        // Code: e.getSource().takeDamage(...)

        // Scenario: I put shield on myself. source = me.
        // I take 10 damage.
        // finalDamage = 5.
        // e.getSource() (me) takes 2 damage (20% of 10).
        // So I take 5 + 2 = 7 damage?

        // That seems like a bug or I misunderstand "Shield Reflect".
        // Maybe "SHIELD_REFLECT" is a debuff put on me by enemy? "When you hit me, you take damage"?
        // No, "Scudo Magico (Mago): riduce il danno del 50%" sound like a defensive buff.

        // Let's look at the code comments in `Combatant.java`:
        // "Scudo Magico (Mago): riduce il danno del 50% e riflette il 20%"
        // "Riflette il 20% del danno ORIGINALE al nemico"

        // But `takeDamage(int dmg)` has no `Combatant attacker` argument!
        // So `e.getSource()` MUST be the "enemy".
        // This means when I activate Shield, I technically need to know who I am fighting?
        // OR the stored `source` in StatusEffect is used as a proxy for "last attacker" or "current target"?
        // But StatusEffect is added via `addStatusEffect`.

        // If I use it as:
        // player.addStatusEffect(new StatusEffect("SHIELD_REFLECT", 1, 0.5f, ENEMY));
        // Then when player takes damage, ENEMY takes 20%.

        // So for the test, I will Set source to `enemy`.

        StatusEffect shieldEffect = new StatusEffect("SHIELD_REFLECT", 1, 0.5f, enemy);
        combatant.addStatusEffect(shieldEffect);

        combatant.takeDamage(10);

        // Player takes 50% = 5
        assertEquals(95, combatant.getCurrentHp()); // 100 - 5

        // Enemy takes 20% = 2
        assertEquals(98, enemy.getCurrentHp()); // 100 - 2

        // Effect should be expired (logic says e.setExpired(true))
        assertTrue(shieldEffect.isExpired());
    }

    @Test
    void testVulnerable() {
        // "VULNERABLE": enemy takes 50% *more* damage (modifier should be 1.5)
        // Code: finalDamage = (int)(dmg * e.getModifier());

        StatusEffect vulnerable = new StatusEffect("VULNERABLE", 1, 1.5f);
        combatant.addStatusEffect(vulnerable);

        combatant.takeDamage(10);

        // Damage = 10 * 1.5 = 15
        assertEquals(85, combatant.getCurrentHp()); // 100 - 15

        // Effect consumed
        assertTrue(vulnerable.isExpired());
    }

    @Test
    void testStun() {
        // "STUN": prevents action correctly?
        // Combatant.java: isStunned() returns true if "STUN" effect exists.

        assertFalse(combatant.isStunned());

        combatant.addStatusEffect(new StatusEffect("STUN", 1));

        assertTrue(combatant.isStunned());
    }
}
