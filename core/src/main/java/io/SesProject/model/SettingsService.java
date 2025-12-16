package io.SesProject.model;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import io.SesProject.model.observer.SettingsObserver;

import java.util.ArrayList;
import java.util.List;

public class SettingsService {

    private List<SettingsObserver> observers;
    private Preferences prefs;

    private float volume;
    private int width;
    private int height;

    private static final String PREF_NAME = "myrpg_settings";

    public SettingsService() {
        this.observers = new ArrayList<>();
        this.prefs = Gdx.app.getPreferences(PREF_NAME);

        this.volume = prefs.getFloat("volume", 1.0f);
        this.width = prefs.getInteger("width", 1280);
        this.height = prefs.getInteger("height", 720);
    }

    public void addObserver(SettingsObserver o) {
        this.observers.add(o);
    }

    public void setVolume(float v) {
        this.volume = v;
        prefs.putFloat("volume", v);
        prefs.flush();
        notifyObserversVolume();
    }

    public float getVolume() { return volume; }

    public void setResolution(int w, int h) {
        this.width = w;
        this.height = h;
        prefs.putInteger("width", w);
        prefs.putInteger("height", h);
        prefs.flush();
        notifyObserversResolution();
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    private void notifyObserversVolume() {
        for (SettingsObserver o : observers) {
            o.onVolumeChanged(volume);
        }
    }

    private void notifyObserversResolution() {
        for (SettingsObserver o : observers) {
            o.onResolutionChanged(width, height);
        }
    }

    public void applySettings() {
        notifyObserversVolume();
        notifyObserversResolution();
    }
}
