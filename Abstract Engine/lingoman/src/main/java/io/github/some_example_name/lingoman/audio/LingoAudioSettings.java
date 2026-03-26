package io.github.some_example_name.lingoman.audio;

import com.badlogic.gdx.math.MathUtils;

import io.github.some_example_name.managers.AudioManager;
import io.github.some_example_name.save.SaveData;

public final class LingoAudioSettings {

    private static final float DEFAULT_VOLUME = 1f;

    private final float masterVolume;
    private final float musicVolume;
    private final float soundVolume;

    private LingoAudioSettings(Builder builder) {
        this.masterVolume = builder.masterVolume;
        this.musicVolume = builder.musicVolume;
        this.soundVolume = builder.soundVolume;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static LingoAudioSettings fromAudioManager(AudioManager audioManager) {
        if (audioManager == null) {
            throw new IllegalArgumentException("audioManager cannot be null.");
        }

        return builder()
            .masterVolume(audioManager.getMasterVolume())
            .musicVolume(audioManager.getMusicVolume())
            .soundVolume(audioManager.getSoundMasterVolume())
            .build();
    }

    public static Builder builderFromSaveData(SaveData saveData) {
        return builder().readFrom(saveData);
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void applyTo(AudioManager audioManager) {
        if (audioManager == null) {
            throw new IllegalArgumentException("audioManager cannot be null.");
        }

        audioManager.setMasterVolume(masterVolume);
        audioManager.setMusicVolume(musicVolume);
        audioManager.setSoundMasterVolume(soundVolume);
    }

    public SaveData toSaveData(String saveId) {
        SaveData saveData = new SaveData(saveId);
        saveData.put("master_volume", masterVolume);
        saveData.put("music_volume", musicVolume);
        saveData.put("sound_volume", soundVolume);
        return saveData;
    }

    public static final class Builder {

        private float masterVolume = DEFAULT_VOLUME;
        private float musicVolume = DEFAULT_VOLUME;
        private float soundVolume = DEFAULT_VOLUME;

        private Builder() {
        }

        public Builder masterVolume(float volume) {
            this.masterVolume = clamp(volume);
            return this;
        }

        public Builder musicVolume(float volume) {
            this.musicVolume = clamp(volume);
            return this;
        }

        public Builder soundVolume(float volume) {
            this.soundVolume = clamp(volume);
            return this;
        }

        public Builder defaultsFrom(AudioManager audioManager) {
            if (audioManager == null) {
                throw new IllegalArgumentException("audioManager cannot be null.");
            }

            return masterVolume(audioManager.getMasterVolume())
                .musicVolume(audioManager.getMusicVolume())
                .soundVolume(audioManager.getSoundMasterVolume());
        }

        public Builder readFrom(SaveData saveData) {
            if (saveData == null) {
                return this;
            }

            Float master = readVolume(saveData.get("master_volume"));
            if (master != null) {
                masterVolume(master);
            }

            Float music = readVolume(saveData.get("music_volume"));
            if (music != null) {
                musicVolume(music);
            }

            Float sound = readVolume(saveData.get("sound_volume"));
            if (sound != null) {
                soundVolume(sound);
            }

            return this;
        }

        public LingoAudioSettings build() {
            return new LingoAudioSettings(this);
        }

        private static Float readVolume(Object rawValue) {
            if (rawValue instanceof Number number) {
                return clamp(number.floatValue());
            }
            if (rawValue instanceof String text) {
                try {
                    return clamp(Float.parseFloat(text.trim()));
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
            return null;
        }

        private static float clamp(float value) {
            return MathUtils.clamp(value, 0f, 1f);
        }
    }
}
