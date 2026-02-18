package io.github.some_example_name.lwjgl3.engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.lwjgl3.engine.scene.SceneManager;
import io.github.some_example_name.lwjgl3.engine.scene.IScene;

public class EngineGame extends ApplicationAdapter {

    private SpriteBatch batch;
    private SceneManager sceneManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        sceneManager = new SceneManager();

        sceneManager.setScene(new io.github.some_example_name.lwjgl3.engine.scene.TestScene());

    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        IScene current = sceneManager.getCurrent();
        if (current != null) {
            current.update(dt);

            batch.begin();
            current.render(batch);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    
}
