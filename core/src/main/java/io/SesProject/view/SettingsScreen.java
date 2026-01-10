package io.SesProject.view;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.SesProject.controller.SettingsController;


public class SettingsScreen extends BaseMenuScreen {

    private SettingsController controller;

    private Slider volumeSlider;
    private SelectBox<String> resolutionBox;
    private TextButton backBtn;

    // Suono di test per l'Acceptance Criteria (può essere un file breve tipo 'click.wav')
    // Se non hai il file, commenta la parte relativa al suono.
    private Sound testSound;

    public SettingsScreen(SettingsController controller) {
        super();
        this.controller = controller;
        buildUI();
        setupValues(); // Imposta i valori iniziali della UI in base al Service

    }

    @Override
    protected void buildUI() {
        rootTable.add(new Label("IMPOSTAZIONI DI GIOCO", skin)).padBottom(40).colspan(2).row();

        // --- VOLUME ---
        rootTable.add(new Label("Volume Audio:", skin)).right().padRight(20);

        // Slider: min 0, max 1, step 0.1, verticale false
        volumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        rootTable.add(volumeSlider).width(300).row();

        // --- RISOLUZIONE ---
        rootTable.add(new Label("Risoluzione Video:", skin)).right().padRight(20).padTop(20);

        resolutionBox = new SelectBox<>(skin);
        resolutionBox.setItems("800x600", "1280x720", "1920x1080");
        rootTable.add(resolutionBox).width(300).padTop(20).row();

        // --- BACK ---
        backBtn = new TextButton("Torna Indietro", skin);
        rootTable.add(backBtn).colspan(2).padTop(50).width(250);

        setupListeners();
    }

    private void setupValues() {
        // Sincronizza UI con i dati attuali del Service
        volumeSlider.setValue(controller.getCurrentVolume());

        String currentRes = controller.getCurrentWidth() + "x" + controller.getCurrentHeight();
        resolutionBox.setSelected(currentRes);
    }

    private void setupListeners() {
        // Listener Volume
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = volumeSlider.getValue();
                controller.updateVolume(val);

                // AC 3: "Verify confirmation sound plays"
                // Riproduciamo il suono solo se lo slider viene rilasciato o ad ogni step
                // (Per semplicità qui suona ad ogni change, ma occhio al volume alto!)
                if (testSound != null && !volumeSlider.isDragging()) {
                    testSound.play(val);
                }
            }
        });

        // Listener Risoluzione
        resolutionBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.updateResolution(resolutionBox.getSelected());
            }
        });

        // Listener Back
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.back();
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        if (testSound != null) testSound.dispose();
    }
}
