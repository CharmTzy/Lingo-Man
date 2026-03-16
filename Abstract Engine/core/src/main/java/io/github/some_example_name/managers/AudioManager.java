package io.github.some_example_name.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

public class AudioManager implements Disposable {

    private final Map<String, Sound> soundEffects = new HashMap<>();
    private final Map<String, Music> musicTracks = new HashMap<>();
    private final Map<String, Float> soundVolumes = new HashMap<>();
    private final Map<String, Long> loopingSoundInstances = new HashMap<>();

    private float masterVolume = 1f;
    private float musicVolume = 1f;
    private float soundMasterVolume = 1f;
    private boolean muted = false;
    private Music currentMusic;

    public void loadSound(String id, String path) {
        validateIdAndPath(id, path);
        if (!Gdx.files.internal(path).exists()) {
            logMissingAsset("sound", id, path);
            return;
        }

        Sound previous = soundEffects.put(id, Gdx.audio.newSound(Gdx.files.internal(path)));
        if (previous != null) {
            Long loopingInstanceId = loopingSoundInstances.remove(id);
            if (loopingInstanceId != null) {
                previous.stop(loopingInstanceId);
            }
            previous.dispose();
        }
        soundVolumes.putIfAbsent(id, 1f);
    }

    public void loadMusic(String id, String path) {
        validateIdAndPath(id, path);
        if (!Gdx.files.internal(path).exists()) {
            logMissingAsset("music", id, path);
            return;
        }

        Music previous = musicTracks.put(id, Gdx.audio.newMusic(Gdx.files.internal(path)));
        if (previous != null) {
            if (currentMusic == previous) {
                currentMusic = null;
            }
            previous.dispose();
        }
    }

    public void playSound(String id, boolean loop) {
        Sound sound = soundEffects.get(id);
        if (sound == null) {
            return;
        }

        long instanceId = sound.play(resolveSoundVolume(id));
        if (loop) {
            sound.setLooping(instanceId, true);
        }
    }

    public void playLoopingSound(String id) {
        Sound sound = soundEffects.get(id);
        if (sound == null || loopingSoundInstances.containsKey(id)) {
            return;
        }

        long instanceId = sound.loop(resolveSoundVolume(id));
        loopingSoundInstances.put(id, instanceId);
    }

    public void stopLoopingSound(String id) {
        if (id == null || id.isBlank()) {
            return;
        }

        Sound sound = soundEffects.get(id);
        Long instanceId = loopingSoundInstances.remove(id);
        if (sound == null || instanceId == null) {
            return;
        }

        sound.stop(instanceId);
    }

    public void playMusic(String id, boolean loop) {
        Music nextMusic = musicTracks.get(id);
        if (nextMusic == null) {
            return;
        }

        if (currentMusic != null && currentMusic != nextMusic) {
            currentMusic.stop();
        }

        currentMusic = nextMusic;
        currentMusic.setLooping(loop);
        currentMusic.setVolume(resolveMusicVolume());
        currentMusic.play();
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void setMusicVolume(float volume) {
        musicVolume = MathUtils.clamp(volume, 0f, 1f);
        if (currentMusic != null) {
            currentMusic.setVolume(resolveMusicVolume());
        }
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMasterVolume(float volume) {
        masterVolume = MathUtils.clamp(volume, 0f, 1f);
        if (currentMusic != null) {
            currentMusic.setVolume(resolveMusicVolume());
        }
        refreshLoopingSoundVolumes();
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setSoundMasterVolume(float volume) {
        soundMasterVolume = MathUtils.clamp(volume, 0f, 1f);
        refreshLoopingSoundVolumes();
    }

    public float getSoundMasterVolume() {
        return soundMasterVolume;
    }

    public void pauseMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public void resumeMusic() {
        if (currentMusic != null) {
            currentMusic.play();
        }
    }

    public void setSoundVolume(String id, float volume) {
        if (id == null || id.isBlank()) {
            return;
        }
        soundVolumes.put(id, MathUtils.clamp(volume, 0f, 1f));
        refreshLoopingSoundVolume(id);
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        if (currentMusic != null) {
            currentMusic.setVolume(resolveMusicVolume());
        }
        refreshLoopingSoundVolumes();
    }

    public boolean isMuted() {
        return muted;
    }

    @Override
    public void dispose() {
        Set<Sound> sounds = new HashSet<>(soundEffects.values());
        for (Sound sound : sounds) {
            if (sound != null) {
                sound.dispose();
            }
        }
        soundEffects.clear();
        soundVolumes.clear();
        loopingSoundInstances.clear();

        Set<Music> music = new HashSet<>(musicTracks.values());
        for (Music track : music) {
            if (track != null) {
                track.dispose();
            }
        }
        musicTracks.clear();
        currentMusic = null;
    }

    private void validateIdAndPath(String id, String path) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Audio id cannot be null or blank.");
        }
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Audio path cannot be null or blank.");
        }
    }

    private void logMissingAsset(String type, String id, String path) {
        if (Gdx.app != null) {
            Gdx.app.log("AudioManager", "Skipping missing " + type + " [" + id + "] at path: " + path);
        }
    }

    private float resolveSoundVolume(String id) {
        return muted ? 0f : MathUtils.clamp(soundVolumes.getOrDefault(id, 1f) * soundMasterVolume * masterVolume, 0f, 1f);
    }

    private float resolveMusicVolume() {
        return muted ? 0f : MathUtils.clamp(masterVolume * musicVolume, 0f, 1f);
    }

    private void refreshLoopingSoundVolumes() {
        for (String id : loopingSoundInstances.keySet()) {
            refreshLoopingSoundVolume(id);
        }
    }

    private void refreshLoopingSoundVolume(String id) {
        Sound sound = soundEffects.get(id);
        Long instanceId = loopingSoundInstances.get(id);
        if (sound == null || instanceId == null) {
            return;
        }
        sound.setVolume(instanceId, resolveSoundVolume(id));
    }
}
