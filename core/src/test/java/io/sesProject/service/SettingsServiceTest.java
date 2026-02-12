package io.sesProject.service;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import io.SesProject.model.SettingsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingsServiceTest {

    @Mock
    private Application mockApp;

    @Mock
    private Preferences mockPreferences;

    private SettingsService settingsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Gdx.app = mockApp;

        when(mockApp.getPreferences("myrpg_settings")).thenReturn(mockPreferences);

        when(mockPreferences.getFloat(eq("volume"), anyFloat())).thenAnswer(invocation -> invocation.getArgument(1));
        when(mockPreferences.getInteger(eq("width"), anyInt())).thenAnswer(invocation -> invocation.getArgument(1));
        when(mockPreferences.getInteger(eq("height"), anyInt())).thenAnswer(invocation -> invocation.getArgument(1));

        settingsService = new SettingsService();
    }

    @AfterEach
    void tearDown() {
        Gdx.app = null;
    }

    @Test
    void testDefaultValues() {
        // Defaults: volume 1.0f, width 1280, height 720
        assertEquals(1.0f, settingsService.getVolume());
        assertEquals(1280, settingsService.getWidth());
        assertEquals(720, settingsService.getHeight());
    }

    @Test
    void testSetAndSaveValues() {
        settingsService.setVolume(0.8f);
        settingsService.setResolution(1920, 1080);

        verify(mockPreferences).putFloat("volume", 0.8f);
        verify(mockPreferences).putInteger("width", 1920);
        verify(mockPreferences).putInteger("height", 1080);
        verify(mockPreferences, atLeastOnce()).flush();

        // Update mock to return new values
        when(mockPreferences.getFloat(eq("volume"), anyFloat())).thenReturn(0.8f);
        when(mockPreferences.getInteger(eq("width"), anyInt())).thenReturn(1920);
        when(mockPreferences.getInteger(eq("height"), anyInt())).thenReturn(1080);

        assertEquals(0.8f, settingsService.getVolume());
        assertEquals(1920, settingsService.getWidth());
        assertEquals(1080, settingsService.getHeight());
    }
}
