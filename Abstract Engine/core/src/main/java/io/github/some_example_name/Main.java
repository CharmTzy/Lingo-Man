package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

/** Single runtime entrypoint shared by all launchers. */
public class Main extends ApplicationAdapter {

    private final EngineBootstrap bootstrap;
    private EngineContext context;

    public Main() {
        this(EngineBootstrap.NO_OP);
    }

    public Main(EngineBootstrap bootstrap) {
        this.bootstrap = bootstrap == null ? EngineBootstrap.NO_OP : bootstrap;
    }

    @Override
    public void create() {
        context = new EngineContext();
        bootstrap.initialize(context);
        BrowserBridge.signalReady();
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        bootstrap.update(context, deltaTime);
        
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

        bootstrap.dispose(context);
        context.getSceneManager().dispose();
        context.getAudioManager().dispose();
        context.getOutputManager().dispose();
    }
}
