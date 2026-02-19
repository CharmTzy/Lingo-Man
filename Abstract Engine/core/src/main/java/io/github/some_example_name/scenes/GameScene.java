package io.github.some_example_name.scenes;

import io.github.some_example_name.EngineContext;

public class GameScene implements Scene {

    private EngineContext context;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
    }

    @Override
    public void enter() {
        System.out.println("[GameScene] Game started");
    }

    @Override
    public void exit() {
        System.out.println("[GameScene] Game paused/ended");
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().isPauseJustPressed()) {
            context.getSceneManager().setActiveScene(SceneId.PAUSE);
        }

        if (context.getInputManager().isActionJustPressed()) {
            context.getSceneManager().setActiveScene(SceneId.GAME_OVER);
        }
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render() {
        context.getOutputManager().clearScreen(0.05f, 0.15f, 0.07f, 1f);
    }
    
    @Override
    public void dispose() {
        System.out.println("[GameScene] Resources disposed");
    }
}