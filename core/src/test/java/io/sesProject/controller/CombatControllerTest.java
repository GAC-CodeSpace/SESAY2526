package io.sesProject.controller;

import io.SesProject.controller.CombatController;
import io.SesProject.model.game.combat.EnemyCombatant;
import io.SesProject.model.game.combat.PlayerCombatant;
import io.SesProject.model.game.npc.NpcData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CombatControllerTest extends ControllerTestBase {

    private CombatController controller;
    private NpcData enemyData;

    @BeforeEach
    void setUp() {
        super.setUpBase();

        enemyData = new NpcData();
        enemyData.setName("Goblin");
        enemyData.setMaxHp(50);
        enemyData.setAttackPower(5);
        enemyData.setXpReward(10);
        enemyData.setKarmaReward(2);

        // CombatController constructor calls initializeEncounter which uses game.getCurrentSession()
        // which is already mocked in base.

        controller = new CombatController(mockGame, mockAuthService, enemyData);
    }

    @Test
    void testInitializationAndTurnQueue() {
        assertNotNull(controller.getHeroes());
        assertEquals(2, controller.getHeroes().size());
        assertEquals(1, controller.getEnemies().size());

        assertNotNull(controller.getCurrentActor());

        // Verify Warrior is first (p1 is Warrior)
        // Note: Logic sorts queue. isWarrior checks archetype "Warrior".
        // My p1 dummy has "Warrior".
        assertTrue(controller.getCurrentActor() instanceof PlayerCombatant);
        assertEquals("Warrior", ((PlayerCombatant)controller.getCurrentActor()).getArchetype());
    }

    @Test
    void testWinCondition() {
        // Force enemy hp to 0
        EnemyCombatant enemy = (EnemyCombatant) controller.getEnemies().get(0);
        enemy.takeDamage(1000); // Kill it

        // We need to trigger checkWinCondition.
        // Logic: executeSkillUsage calls checkWinCondition at end.
        // OR simulateEnemyTurn calls checkLoseCondition then... nextTurn?

        // We can't easily access checkWinCondition directly as it is private.
        // But we can check if CombatReward is generated after a turn or action that kills enemy.

        // Let's use a public method to trigger check.
        // But private methods are hard to trigger without side effects.

        // CombatController.executeSkillUsage() calls checkWinCondition().
        // But we need to use a skill.
        // Mocking PlayerCombatant/Skill to force kill?

        // Alternatively, since we can't easily invoke private methods, we rely on the state change.
        // Actually, if enemy is dead, next time startTurn() runs? No.

        // Let's look at executeSkillUsage:
        // if (!checkWinCondition()) { nextTurn(); }

        // So if we are in a state where enemy is dead, and we call executeSkillUsage...
        // ... well checkWinCondition will return true and call game.changeAppState(VictoryState).

        // But to call executeSkillUsage we need to verify isPlayerTurn().
        // p1 (Warrior) is currentActor.

        // Mock a skill
        io.SesProject.model.game.Skill mockSkill = mock(io.SesProject.model.game.Skill.class);
        when(mockSkill.isReady()).thenReturn(true);
        when(mockSkill.isUnlocked()).thenReturn(true);
        when(mockSkill.getName()).thenReturn("Attack");

        // Execute
        controller.executeSkillUsage(mockSkill);

        // Verify verify(mockSkill).use(...) was called
        verify(mockSkill).use(any(), any(), anyList());

        // Since enemy was killed BEFORE use (or by use), we expect win.
        // Enemy is already dead (HP=0).
        // checkWinCondition() should be true.
        // It shoud call game.changeAppState(...)
        verify(mockGame).changeAppState(any(io.SesProject.controller.state.VictoryState.class));

        assertNotNull(controller.getCombatReward());
        assertEquals(10, controller.getCombatReward().getTotalXp());
    }
}
