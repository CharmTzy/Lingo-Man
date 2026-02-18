package io.github.some_example_name.lwjgl3.engine.scene;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface IScene {
    void onEnter();
    void onExit();
    void update(float dt);
    void render(Batch batch);
}
