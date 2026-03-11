package io.github.some_example_name.lingoman.scenes;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.lingoman.LingoInputActions;
import io.github.some_example_name.lingoman.LingoSceneIds;
import io.github.some_example_name.lingoman.LingoSession;
import io.github.some_example_name.scenes.Scene;

public class GameOverScene implements Scene {

    private EngineContext context;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
    }

    @Override
    public void enter() {
        System.out.println("[LingoMan] Game over");
    }

    @Override
    public void exit() {
        System.out.println("[LingoMan] Game over exit");
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().isActionJustPressed(LingoInputActions.GAME_RESTART)) {
            context.getSceneManager().setActiveScene(LingoSceneIds.GAME);
        }
        if (context.getInputManager().isActionJustPressed(LingoInputActions.GAME_MENU)) {
            context.getSceneManager().setActiveScene(LingoSceneIds.MENU);
        }
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render() {
        context.getOutputManager().clearScreen(0.15f, 0.05f, 0.05f, 1f);
        String result = LingoSession.get().getGameState().getLastResult();
        if (result == null || result.isBlank()) {
            result = "GAME OVER";
        }
        context.getOutputManager().drawText(result, 200f, 360f);
        context.getOutputManager().drawText("R: Restart", 230f, 300f);
        context.getOutputManager().drawText("M or ESC: Menu", 210f, 260f);
    }

    @Override
    public void dispose() {
        System.out.println("[LingoMan] Game over disposed");
    }
}