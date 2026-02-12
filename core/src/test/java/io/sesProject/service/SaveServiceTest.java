package io.sesProject.service;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import io.SesProject.model.SaveMetadata;
import io.SesProject.model.memento.GameSessionMemento;
import io.SesProject.model.memento.PlayerCharacterMemento;
import io.SesProject.service.SaveService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SaveServiceTest {

    @Mock
    private Files mockFiles;

    @Mock
    private FileHandle mockRootHandle;

    @Mock
    private FileHandle mockSaveFileHandle;

    @Mock
    private FileHandle mockUserDirHandle;

    private SaveService saveService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Gdx.files = mockFiles;

        // Setup common mock behavior for ROOT_DIR checks in constructor
        when(mockFiles.local("saves/")).thenReturn(mockRootHandle);
        when(mockRootHandle.exists()).thenReturn(true);

        saveService = new SaveService();
    }

    @AfterEach
    void tearDown() {
        Gdx.files = null;
    }

    @Test
    void testCreateNewSaveSlot_Success() {
        String username = "Player1";

        // Mock file checks for finding a free slot
        // 1st check: save_1.json -> exists
        FileHandle slot1 = mock(FileHandle.class);
        when(slot1.exists()).thenReturn(true);
        when(mockFiles.local("saves/" + username + "/save_1.json")).thenReturn(slot1);

        // 2nd check: save_2.json -> does not exist (so it will use slot 2)
        FileHandle slot2 = mock(FileHandle.class);
        when(slot2.exists()).thenReturn(false);
        when(mockFiles.local("saves/" + username + "/save_2.json")).thenReturn(slot2);

        // Prepare memento
        GameSessionMemento memento = new GameSessionMemento();

        // Execution
        int slotId = saveService.createNewSaveSlot(memento, username);

        assertEquals(2, slotId);

        // Verify write
        verify(slot2).writeString(anyString(), eq(false));
    }

    @Test
    void testSaveGame() {
        String username = "Player1";
        int slotId = 5;
        GameSessionMemento memento = new GameSessionMemento();
        memento.player1 = new PlayerCharacterMemento();
        memento.player1.name = "Hero";

        FileHandle slotFile = mock(FileHandle.class);
        when(mockFiles.local("saves/" + username + "/save_" + slotId + ".json")).thenReturn(slotFile);

        saveService.saveGame(memento, username, slotId);

        verify(slotFile).writeString(contains("Hero"), eq(false));
    }

    @Test
    void testLoadGame_FileExists() {
        String username = "Player1";
        int slotId = 1;
        String jsonContent = "{ \"class\": \"session\", \"player1\": { \"class\": \"character\", \"name\": \"LoadedHero\" } }";

        FileHandle slotFile = mock(FileHandle.class);
        when(mockFiles.local("saves/" + username + "/save_" + slotId + ".json")).thenReturn(slotFile);
        when(slotFile.exists()).thenReturn(true);
        when(slotFile.readString()).thenReturn(jsonContent);

        GameSessionMemento result = (GameSessionMemento) saveService.loadGame(username, slotId);

        assertNotNull(result);
        assertNotNull(result.player1);
        assertEquals("LoadedHero", result.player1.name);
    }

    @Test
    void testLoadGame_FileDoesNotExist() {
        String username = "Player1";
        int slotId = 99;

        FileHandle slotFile = mock(FileHandle.class);
        when(mockFiles.local("saves/" + username + "/save_" + slotId + ".json")).thenReturn(slotFile);
        when(slotFile.exists()).thenReturn(false);

        assertNull(saveService.loadGame(username, slotId));
    }

    @Test
    void testGetSaveSlots() {
        String username = "Player1";

        // Mock user directory
        when(mockFiles.local("saves/" + username)).thenReturn(mockUserDirHandle);
        when(mockUserDirHandle.exists()).thenReturn(true);
        when(mockUserDirHandle.isDirectory()).thenReturn(true);

        // Mock files in directory
        FileHandle file1 = mock(FileHandle.class);
        when(file1.name()).thenReturn("save_1.json");
        when(file1.readString()).thenReturn("{ \"class\": \"session\", \"creationDate\": \"2023-01-01\", \"player1\": { \"class\": \"character\", \"level\": 5 } }");

        FileHandle file2 = mock(FileHandle.class);
        when(file2.name()).thenReturn("save_3.json"); // Intentionally skipping 2
        when(file2.readString()).thenReturn("{ \"class\": \"session\", \"creationDate\": \"2023-01-02\", \"player1\": { \"class\": \"character\", \"level\": 10 } }");

        when(mockUserDirHandle.list(".json")).thenReturn(new FileHandle[]{file1, file2});

        // Execute
        List<SaveMetadata> slots = saveService.getSaveSlots(username);

        assertEquals(2, slots.size());

        assertEquals(1, slots.get(0).getSlotId());
        assertEquals("Lvl 5", slots.get(0).getDetails());

        assertEquals(3, slots.get(1).getSlotId());
        assertEquals("Lvl 10", slots.get(1).getDetails());
    }
}
