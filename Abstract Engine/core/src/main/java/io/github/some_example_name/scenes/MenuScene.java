package io.github.some_example_name.scenes;

import io.github.some_example_name.EngineContext;

public class MenuScene implements Scene {

    private EngineContext context;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
    }

    @Override
    public void enter() {
        System.out.println("[MenuScene] Entered - Press ENTER to start game");
    }

    @Override
    public void exit() {
        System.out.println("[MenuScene] Exiting menu");
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().isEnterJustPressed()) {
            context.getSceneManager().setActiveScene(SceneId.GAME);
        }
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render() {
        context.getOutputManager().clearScreen(0.10f, 0.10f, 0.15f, 1f);
    }

    @Override
    public void dispose() {
        System.out.println("[MenuScene] Resources disposed");
    }
}