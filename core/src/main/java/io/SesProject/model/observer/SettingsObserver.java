package io.SesProject.model.observer;

public interface SettingsObserver {
    void onVolumeChanged(float volume);
    void onResolutionChanged(int width, int height);
}
