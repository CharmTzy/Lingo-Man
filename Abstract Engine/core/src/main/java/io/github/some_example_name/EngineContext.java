package io.github.some_example_name;

import io.github.some_example_name.managers.SceneManager;

public class EngineContext {

    private final SceneManager sceneManager;

    public EngineContext(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }
}
