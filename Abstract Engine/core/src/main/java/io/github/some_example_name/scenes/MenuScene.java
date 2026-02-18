package io.github.some_example_name.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.EngineContext;

public class MenuScene implements Scene {

    private EngineContext context;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
    }

    @Override public void enter() {}
    @Override public void exit()  {}

    @Override
    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            context.getSceneManager().setActiveScene(SceneId.GAME);
        }
    }

    @Override public void update(float deltaTime) {}

    @Override
    public void render() {
        ScreenUtils.clear(0.10f, 0.10f, 0.15f, 1f);
    }

    @Override public void dispose() {}
}
