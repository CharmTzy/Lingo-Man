package io.github.some_example_name.systems;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.some_example_name.managers.SceneManager;
import io.github.some_example_name.scenes.GameOverScene;
import io.github.some_example_name.scenes.GameScene;
import io.github.some_example_name.scenes.MenuScene;
import io.github.some_example_name.scenes.PauseScene;
import io.github.some_example_name.scenes.SceneId;

/** Single runtime entrypoint shared by all launchers. */
public class Main extends ApplicationAdapter {

    private EngineContext context;

    @Override
    public void create() {
        context = new EngineContext();
        registerScenes(context.getSceneManager());
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        context.getSceneManager().update(deltaTime);
        context.getSceneManager().render();
    }

    @Override
    public void dispose() {
        if (context == null) {
            return;
        }

        context.getSceneManager().dispose();
        context.getOutputManager().dispose();
    }

    private void registerScenes(SceneManager sceneManager) {
        sceneManager.registerScene(SceneId.MENU, new MenuScene());
        sceneManager.registerScene(SceneId.GAME, new GameScene());
        sceneManager.registerScene(SceneId.PAUSE, new PauseScene());
        sceneManager.registerScene(SceneId.GAME_OVER, new GameOverScene());
        sceneManager.setActiveScene(SceneId.MENU);
    }
}
