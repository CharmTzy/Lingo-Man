package io.github.some_example_name.scenes;

import io.github.some_example_name.EngineContext;

public interface Scene {
    void initialize(EngineContext context);

    void enter();
    void exit();

    void handleInput();

    void update(float deltaTime);
    void render();

    void dispose();
}
