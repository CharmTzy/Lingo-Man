package io.github.some_example_name.lingoman.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.lingoman.LingoAudio;
import io.github.some_example_name.lingoman.LingoInputActions;
import io.github.some_example_name.lingoman.LingoSceneIds;
import io.github.some_example_name.scenes.Scene;

public class MainMenuScene implements Scene {

    private static final Color BACKGROUND = new Color(0.05f, 0.05f, 0.09f, 1f);
    private static final Color GRID_BAND = new Color(0.12f, 0.16f, 0.26f, 0.45f);
    private static final Color GLOW_TOP = new Color(0.16f, 0.32f, 0.40f, 0.28f);
    private static final Color GLOW_BOTTOM = new Color(0.39f, 0.14f, 0.08f, 0.32f);
    private static final Color TITLE_PANEL_FILL = new Color(0.07f, 0.10f, 0.15f, 0.96f);
    private static final Color TITLE_PANEL_BORDER = new Color(0.99f, 0.83f, 0.31f, 1f);
    private static final Color TITLE_TEXT = new Color(0.98f, 0.95f, 0.84f, 1f);
    private static final Color TITLE_ACCENT = new Color(0.98f, 0.61f, 0.27f, 1f);
    private static final Color TEXT_MUTED = new Color(0.74f, 0.79f, 0.84f, 1f);
    private static final Color START_FILL = new Color(0.13f, 0.18f, 0.24f, 1f);
    private static final Color START_TEXT = new Color(0.98f, 0.96f, 0.90f, 1f);
    private static final Color START_SHADOW = new Color(0.08f, 0.08f, 0.10f, 1f);

    private EngineContext context;
    private float pulseTimer;
    private final Color pulsingBorder = new Color();
    private final Color pulsingFill = new Color();

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
    }

    @Override
    public void enter() {
        pulseTimer = 0f;
        context.getAudioManager().playMusic(LingoAudio.BGM_MENU, true);
    }

    @Override
    public void exit() {
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().isActionJustPressed(LingoInputActions.MENU_CONFIRM)) {
            context.getAudioManager().playSound(LingoAudio.SFX_MENU_NAVIGATE, false);
            context.getSceneManager().setActiveScene(LingoSceneIds.MENU);
        }
    }

    @Override
    public void update(float deltaTime) {
        pulseTimer += deltaTime;
    }

    @Override
    public void render() {
        context.getOutputManager().clearScreen(BACKGROUND.r, BACKGROUND.g, BACKGROUND.b, BACKGROUND.a);
        drawBackdrop();
        drawTitlePanel();
        drawStartPrompt();
        context.getOutputManager().drawTextCentered("ARCADE WORD HUNT", 320f, 102f, TEXT_MUTED);
        context.getOutputManager().drawTextCentered("PRESS ENTER TO START", 320f, 74f, TEXT_MUTED);
    }

    @Override
    public void dispose() {
    }

    private void drawBackdrop() {
        context.getOutputManager().drawRect(0f, 360f, 640f, 120f, GLOW_TOP);
        context.getOutputManager().drawRect(0f, 0f, 640f, 138f, GLOW_BOTTOM);

        for (int x = 20; x < 640; x += 40) {
            context.getOutputManager().drawRect(x, 118f, 8f, 244f, GRID_BAND);
        }
        for (int y = 138; y < 360; y += 34) {
            context.getOutputManager().drawRect(24f, y, 592f, 6f, GRID_BAND);
        }
    }

    private void drawTitlePanel() {
        context.getOutputManager().drawPanel(118f, 248f, 404f, 132f, TITLE_PANEL_FILL, TITLE_PANEL_BORDER);
        context.getOutputManager().drawRect(140f, 344f, 360f, 10f, TITLE_ACCENT);
        context.getOutputManager().drawTextCenteredScaled("LINGO", 320f, 330f, TITLE_TEXT, 2.1f);
        context.getOutputManager().drawTextCenteredScaled("MAN", 320f, 286f, TITLE_ACCENT, 2.1f);
    }

    private void drawStartPrompt() {
        float pulse = 0.5f + 0.5f * MathUtils.sin(pulseTimer * 3.6f);
        pulsingBorder.set(
            MathUtils.lerp(0.78f, 1.00f, pulse),
            MathUtils.lerp(0.52f, 0.89f, pulse),
            MathUtils.lerp(0.21f, 0.47f, pulse),
            1f
        );
        pulsingFill.set(
            MathUtils.lerp(START_FILL.r, 0.24f, pulse * 0.35f),
            MathUtils.lerp(START_FILL.g, 0.26f, pulse * 0.35f),
            MathUtils.lerp(START_FILL.b, 0.18f, pulse * 0.20f),
            1f
        );

        context.getOutputManager().drawPanel(166f, 154f, 308f, 58f, 5f, pulsingFill, pulsingBorder);
        context.getOutputManager().drawRect(182f, 170f, 276f, 6f, START_SHADOW);
        context.getOutputManager().drawTextCenteredScaled("START", 320f, 192f, START_TEXT, 1.35f);
    }
}
