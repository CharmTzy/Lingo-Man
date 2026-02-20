package io.github.some_example_name.managers;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.scenes.Scene;

public class SceneManager {

    private static final float MUSIC_VOLUME_STEP = 0.1f;

    private final EngineContext context;

    private final Map<String, Scene> scenes = new LinkedHashMap<>();

    private String activeSceneId;
    private Scene activeScene;

    private String pendingSceneId;

    private String musicMuteActionId;
    private String musicVolumeUpActionId;
    private String musicVolumeDownActionId;

    public SceneManager(EngineContext context) {
        this.context = context;
    }

    public void registerScene(String id, Scene scene) {
        if (id == null || id.isBlank() || scene == null) {
            throw new IllegalArgumentException("Scene id and Scene cannot be null/blank.");
        }
        scenes.put(id, scene);
        scene.initialize(context);
    }

    public void setActiveScene(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Scene id cannot be null/blank.");
        if (!scenes.containsKey(id)) throw new IllegalStateException("Scene not registered: " + id);
        pendingSceneId = id;
    }

    public String getActiveSceneId() {
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

    public void configureGlobalAudioActions(String muteActionId, String volumeUpActionId, String volumeDownActionId) {
        this.musicMuteActionId = normalizeActionId(muteActionId);
        this.musicVolumeUpActionId = normalizeActionId(volumeUpActionId);
        this.musicVolumeDownActionId = normalizeActionId(volumeDownActionId);
    }

    public void dispose() {
        for (Scene scene : scenes.values()) {
            if (scene != null) scene.dispose();
        }
    }

    private void switchTo(String id) {
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

        if (isActionPressed(input, musicMuteActionId)) {
            audio.setMuted(!audio.isMuted());
        }

        if (isActionPressed(input, musicVolumeUpActionId)) {
            audio.setMuted(false);
            audio.setMusicVolume(audio.getMusicVolume() + MUSIC_VOLUME_STEP);
        }

        if (isActionPressed(input, musicVolumeDownActionId)) {
            audio.setMuted(false);
            audio.setMusicVolume(audio.getMusicVolume() - MUSIC_VOLUME_STEP);
        }
    }

    private boolean isActionPressed(InputManager input, String actionId) {
        return actionId != null && input.isActionJustPressed(actionId);
    }

    private String normalizeActionId(String actionId) {
        if (actionId == null || actionId.isBlank()) {
            return null;
        }
        return actionId;
    }
}
