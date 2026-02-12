package io.sesProject.controller;

import io.SesProject.RpgGame;
import io.SesProject.model.GameSession;
import io.SesProject.model.PlayerCharacter;
import io.SesProject.service.AuthService;
import io.SesProject.service.SystemFacade;
import io.SesProject.service.AudioManager;
import com.badlogic.gdx.assets.AssetManager;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public abstract class ControllerTestBase {

    @Mock protected RpgGame mockGame;
    @Mock protected AuthService mockAuthService;
    @Mock protected GameSession mockSession;
    @Mock protected SystemFacade mockFacade;
    @Mock protected AudioManager mockAudio;
    @Mock protected AssetManager mockAssetManager;

    protected PlayerCharacter p1;
    protected PlayerCharacter p2;

    @BeforeEach
    public void setUpBase() {
        MockitoAnnotations.openMocks(this);

        // Setup common mock behavior
        when(mockGame.getSystemFacade()).thenReturn(mockFacade);
        when(mockFacade.getAudioManager()).thenReturn(mockAudio);
        when(mockFacade.getAssetManager()).thenReturn(mockAssetManager);
        when(mockGame.getCurrentSession()).thenReturn(mockSession);

        // Setup dummy players
        p1 = new PlayerCharacter("Hero", "Warrior");
        p2 = new PlayerCharacter("Mage", "Mage");
        when(mockSession.getP1()).thenReturn(p1);
        when(mockSession.getP2()).thenReturn(p2);
    }
}
