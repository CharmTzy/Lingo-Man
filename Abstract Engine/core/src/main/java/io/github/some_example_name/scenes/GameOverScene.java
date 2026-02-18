package io.github.some_example_name.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.EngineContext;

public class GameOverScene implements Scene {

    private EngineContext context;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
    }

    @Override public void enter() {}
    @Override public void exit()  {}

    @Override
    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            context.getSceneManager().setActiveScene(SceneId.GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            context.getSceneManager().setActiveScene(SceneId.MENU);
        }
    }

    @Override public void update(float deltaTime) {}

    @Override
    public void render() {
        ScreenUtils.clear(0.20f, 0.05f, 0.05f, 1f);
    }

    @Override public void dispose() {}
}
