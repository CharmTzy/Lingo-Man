package io.github.some_example_name.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.EngineContext;

public class GameScene implements Scene {

    private EngineContext context;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
    }

    @Override public void enter() {}
    @Override public void exit()  {}

    @Override
    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            context.getSceneManager().setActiveScene(SceneId.PAUSE);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            context.getSceneManager().setActiveScene(SceneId.GAME_OVER);
        }
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.05f, 0.15f, 0.07f, 1f);
    }

    @Override public void dispose() {}
}
