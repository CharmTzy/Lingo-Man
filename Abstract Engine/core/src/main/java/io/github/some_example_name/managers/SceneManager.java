package io.github.some_example_name.managers;

import java.util.EnumMap;
import java.util.Map;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.scenes.Scene;
import io.github.some_example_name.scenes.SceneId;

public class SceneManager {

    private static final float MUSIC_VOLUME_STEP = 0.1f;

    private final EngineContext context;

    private final Map<SceneId, Scene> scenes = new EnumMap<>(SceneId.class);

    private SceneId activeSceneId;
    private Scene activeScene;

    private SceneId pendingSceneId;

    public SceneManager(EngineContext context) {
        this.context = context;
    }

    public void registerScene(SceneId id, Scene scene) {
        if (id == null || scene == null) {
            throw new IllegalArgumentException("SceneId and Scene cannot be null.");
        }
        scenes.put(id, scene);
        scene.initialize(context);
    }

    public void setActiveScene(SceneId id) {
        if (id == null) throw new IllegalArgumentException("SceneId cannot be null.");
        if (!scenes.containsKey(id)) throw new IllegalStateException("Scene not registered: " + id);
        pendingSceneId = id;
    }

    public SceneId getActiveSceneId() {
        return activeSceneId;
    }

    public Scene getActiveScene() {
        return activeScene;
    }

    public void update(float deltaTime) {
        if (pendingSceneId != null) {
            switchTo(pendingSceneId);
            pendingSceneId = null;
        }

        handleGlobalInput();

        if (activeScene == null) return;

        activeScene.handleInput();
        activeScene.update(deltaTime);
    }

    public void render() {
        if (activeScene == null) return;
        activeScene.render();
    }

    public void dispose() {
        for (Scene scene : scenes.values()) {
            if (scene != null) scene.dispose();
        }
    }

    private void switchTo(SceneId id) {
        Scene next = scenes.get(id);
        if (next == null) throw new IllegalStateException("Scene not registered: " + id);

        if (activeScene != null) activeScene.exit();

        activeSceneId = id;
        activeScene = next;

        activeScene.enter();
    }

    private void handleGlobalInput() {
        InputManager input = context.getInputManager();
        AudioManager audio = context.getAudioManager();

        if (input.isMusicMuteToggleJustPressed()) {
            audio.setMuted(!audio.isMuted());
        }

        if (input.isMusicVolumeUpJustPressed()) {
            audio.setMuted(false);
            audio.setMusicVolume(audio.getMusicVolume() + MUSIC_VOLUME_STEP);
        }

        if (input.isMusicVolumeDownJustPressed()) {
            audio.setMuted(false);
            audio.setMusicVolume(audio.getMusicVolume() - MUSIC_VOLUME_STEP);
        }
    }
}
