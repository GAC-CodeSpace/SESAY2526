package io.sesProject.service.npc;

import io.SesProject.model.game.npc.EnemyTemplate;
import io.SesProject.service.npc.Bestiary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BestiaryTest {

    @Test
    void testGetMinionForLevel() {
        // Level 1: Scheletro
        EnemyTemplate minionLvl1 = Bestiary.getMinionForLevel(1);

        assertNotNull(minionLvl1);
        // We can't be 100% sure of name if random, but for level 1 it is hardcoded list of 1.
        assertEquals("Scheletro", minionLvl1.name);
    }

    @Test
    void testGetBossForLevel() {
        // Level 1 Boss
        EnemyTemplate bossLvl1 = Bestiary.getBossForLevel(1);
        assertNotNull(bossLvl1);
        assertEquals("Scheletro Gigante", bossLvl1.name);
    }
}
