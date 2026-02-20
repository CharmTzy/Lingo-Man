package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.some_example_name.demo.DemoBootstrap;

/** Single runtime entrypoint shared by all launchers. */
public class Main extends ApplicationAdapter {

    private EngineContext context;

    @Override
    public void create() {
        context = new EngineContext();
        DemoBootstrap.initialize(context);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        
        // 1. Update logic
        context.getSceneManager().update(deltaTime);
        
        // 2. Render logic (Must begin and end the batch here)
        context.getOutputManager().begin();
        context.getSceneManager().render();
        context.getOutputManager().end();
    }

    @Override
    public void pause() {
        if (context != null) {
            context.getAudioManager().pauseMusic();
        }
    }

    @Override
    public void resume() {
        if (context != null) {
            context.getAudioManager().resumeMusic();
        }
    }

    @Override
    public void dispose() {
        if (context == null) {
            return;
        }

        context.getSceneManager().dispose();
        context.getAudioManager().dispose();
        context.getOutputManager().dispose();
    }
}
