package io.github.some_example_name.lingoman.audio;

import com.badlogic.gdx.Gdx;

import io.github.some_example_name.managers.AudioManager;
import io.github.some_example_name.save.ISaveable;
import io.github.some_example_name.save.SaveData;

public final class LingoAudioSettingsSaveable implements ISaveable {

    private static final String SAVE_ID = "lingoman_audio_settings";

    private final AudioManager audioManager;

    public LingoAudioSettingsSaveable(AudioManager audioManager) {
        if (audioManager == null) {
            throw new IllegalArgumentException("audioManager cannot be null.");
        }
        this.audioManager = audioManager;
    }

    @Override
    public String getSaveId() {
        return SAVE_ID;
    }

    @Override
    public SaveData writeSaveData() {
        return LingoAudioSettings.fromAudioManager(audioManager).toSaveData(getSaveId());
    }

    @Override
    public void readSaveData(SaveData saveData) {
        LingoAudioSettings settings = LingoAudioSettings.builder()
            .defaultsFrom(audioManager)
            .readFrom(saveData)
            .build();

        if (saveData != null) {
            logIfMissingOrInvalid(saveData, "master_volume");
            logIfMissingOrInvalid(saveData, "music_volume");
            logIfMissingOrInvalid(saveData, "sound_volume");
        }

        settings.applyTo(audioManager);
    }

    private void logIfMissingOrInvalid(SaveData saveData, String key) {
        if (saveData.containsKey(key) && isSupportedValue(saveData.get(key))) {
            return;
        }

        String message = saveData.containsKey(key)
            ? "Ignoring invalid audio setting '" + key + "' for save '" + getSaveId() + "'."
            : "Audio setting '" + key + "' missing for save '" + getSaveId() + "'. Using default/current value.";

        if (Gdx.app != null) {
            Gdx.app.log("LingoAudioSettingsSaveable", message);
        } else {
            System.out.println("[LingoAudioSettingsSaveable] " + message);
        }
    }

    private boolean isSupportedValue(Object rawValue) {
        if (rawValue instanceof Number) {
            return true;
        }
        if (rawValue instanceof String text) {
            try {
                Float.parseFloat(text.trim());
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
        return false;
    }
}
