package io.github.some_example_name.managers;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.scenes.Scene;

public class SceneManager {

    private final EngineContext context;

    private final Map<String, Scene> scenes = new LinkedHashMap<>();

    private String activeSceneId;
    private Scene activeScene;

    private String pendingSceneId;

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

    private void switchTo(String id) {
        Scene next = scenes.get(id);
        if (next == null) throw new IllegalStateException("Scene not registered: " + id);

        if (activeScene != null) activeScene.exit();

        activeSceneId = id;
        activeScene = next;

        activeScene.enter();
    }
}
