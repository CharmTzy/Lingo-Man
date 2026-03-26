package io.github.some_example_name.lingoman.scenes;

import com.badlogic.gdx.graphics.Color;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.lingoman.LingoInputActions;
import io.github.some_example_name.lingoman.LingoSceneIds;
import io.github.some_example_name.lingoman.LingoSession;
import io.github.some_example_name.lingoman.model.WordBank;
import io.github.some_example_name.scenes.Scene;

public class GameOverScene implements Scene {

    private static final Color BACKGROUND = new Color(0.02f, 0.04f, 0.12f, 1f);
    private static final Color BACKDROP_BAND = new Color(0.04f, 0.16f, 0.32f, 0.42f);
    private static final Color BACKDROP_STRIPE = new Color(1.00f, 0.76f, 0.22f, 0.20f);
    private static final Color PANEL_FILL = new Color(0.04f, 0.09f, 0.19f, 0.96f);
    private static final Color PANEL_BORDER = new Color(0.34f, 0.91f, 1.00f, 1f);
    private static final Color SUCCESS = new Color(1.00f, 0.84f, 0.30f, 1f);
    private static final Color FAILURE = new Color(1.00f, 0.55f, 0.40f, 1f);
    private static final Color TEXT_PRIMARY = new Color(0.95f, 0.98f, 1.00f, 1f);
    private static final Color TEXT_MUTED = new Color(0.70f, 0.86f, 0.95f, 1f);

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
        context.getOutputManager().clearScreen(BACKGROUND.r, BACKGROUND.g, BACKGROUND.b, BACKGROUND.a);
        String result = LingoSession.get().getGameState().getLastResult();
        if (result == null || result.isBlank()) {
            result = "GAME OVER";
        }
        String word = LingoSession.get().getGameState().getTargetWord();
        String meaning = WordBank.meaningFor(word);
        String difficulty = LingoSession.get().getGameState().getDifficulty().name();
        int foundCount = LingoSession.get().getGameState().getFoundWordsCount();
        boolean success = "WORD COMPLETE".equalsIgnoreCase(result);
        Color resultColor = success ? SUCCESS : FAILURE;

        context.getOutputManager().drawRect(0f, 338f, 640f, 142f, BACKDROP_BAND);
        context.getOutputManager().drawRect(52f, 334f, 536f, 10f, BACKDROP_STRIPE);
        context.getOutputManager().drawRect(52f, 124f, 536f, 10f, BACKDROP_STRIPE);
        context.getOutputManager().drawPanel(108f, 134f, 424f, 190f, PANEL_FILL, PANEL_BORDER);
        context.getOutputManager().drawTextCenteredWithShadow("ROUND OVER", 320f, 298f, TEXT_MUTED);
        context.getOutputManager().drawTextCenteredWithShadow(result, 320f, 262f, resultColor);
        context.getOutputManager().drawTextCentered("Word: " + word + "    Mode: " + difficulty, 320f, 220f, TEXT_PRIMARY);
        context.getOutputManager().drawTextCentered("Meaning: " + meaning, 320f, 202f, TEXT_MUTED);
        context.getOutputManager().drawTextCentered("Words found total: " + foundCount, 320f, 184f, TEXT_MUTED);
        context.getOutputManager().drawTextCentered("R restart    M or ESC menu", 320f, 166f, TEXT_MUTED);
    }

    @Override
    public void dispose() {
        System.out.println("[LingoMan] Game over disposed");
    }
}
