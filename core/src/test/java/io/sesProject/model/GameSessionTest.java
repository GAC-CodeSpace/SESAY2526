package io.sesProject.model;

import io.SesProject.model.GameSession;
import io.SesProject.model.memento.GameSessionMemento;
import io.SesProject.model.memento.Memento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    private GameSession session;

    @BeforeEach
    void setUp() {
        session = new GameSession(true); // P1 is Tank
    }

    @Test
    void testInitialization_P1Tank() {
        GameSession s = new GameSession(true);
        assertEquals("Warrior", s.getP1().getArchetype());
        assertEquals("Mage", s.getP2().getArchetype());

        assertEquals(100f, s.getP1().getX());
        assertEquals(100f, s.getP1().getY());
    }

    @Test
    void testInitialization_P1Mage() {
        GameSession s = new GameSession(false);
        assertEquals("Mage", s.getP1().getArchetype());
        assertEquals("Warrior", s.getP2().getArchetype());
    }

    @Test
    void testMapNameManagement() {
        assertEquals("casa/casa.tmx", session.getCurrentMapName());

        session.setCurrentMapName("dungeon1.tmx");
        assertEquals("dungeon1.tmx", session.getCurrentMapName());
    }

    @Test
    void testNewGameFlag() {
        assertFalse(session.isNewGame());

        session.setIsNewGame(true);
        assertTrue(session.isNewGame());
    }

    @Test
    void testSaveAndRestore() {
        // Setup state
        session.setCurrentMapName("town.tmx");
        session.setIsNewGame(true);
        session.getP1().addExperience(50); // Lvl 2

        // Save
        Memento memento = session.save();
        assertTrue(memento instanceof GameSessionMemento);
        GameSessionMemento gsm = (GameSessionMemento) memento;

        // Restore to a new session
        GameSession newSession = new GameSession();
        newSession.restore(gsm);

        // Verify
        assertEquals("town.tmx", newSession.getCurrentMapName());
        assertTrue(newSession.isNewGame());
        assertEquals(2, newSession.getP1().getLevel());
    }
}
