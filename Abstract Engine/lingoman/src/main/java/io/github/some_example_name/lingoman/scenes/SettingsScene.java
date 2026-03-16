package io.github.some_example_name.lingoman.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.lingoman.LingoInputActions;
import io.github.some_example_name.lingoman.LingoSession;
import io.github.some_example_name.managers.AudioManager;
import io.github.some_example_name.scenes.Scene;

public class SettingsScene implements Scene {

    private static final Color BACKGROUND = new Color(0.08f, 0.09f, 0.13f, 1f);
    private static final Color PANEL_FILL = new Color(0.09f, 0.11f, 0.16f, 0.96f);
    private static final Color PANEL_BORDER = new Color(0.94f, 0.78f, 0.28f, 1f);
    private static final Color ROW_FILL = new Color(0.14f, 0.17f, 0.22f, 0.98f);
    private static final Color ROW_SELECTED_FILL = new Color(0.96f, 0.79f, 0.26f, 1f);
    private static final Color ROW_BORDER = new Color(0.31f, 0.37f, 0.42f, 1f);
    private static final Color ROW_SELECTED_BORDER = new Color(1.00f, 0.89f, 0.47f, 1f);
    private static final Color BAR_BACKGROUND = new Color(0.19f, 0.22f, 0.26f, 1f);
    private static final Color BAR_FILL = new Color(0.38f, 0.82f, 0.59f, 1f);
    private static final Color TEXT_PRIMARY = new Color(0.96f, 0.96f, 0.92f, 1f);
    private static final Color TEXT_MUTED = new Color(0.71f, 0.76f, 0.77f, 1f);
    private static final Color TEXT_DARK = new Color(0.10f, 0.10f, 0.08f, 1f);

    private static final float VOLUME_STEP = 0.1f;

    private EngineContext context;
    private int selectedRow;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
    }

    @Override
    public void enter() {
        selectedRow = 0;
    }

    @Override
    public void exit() {
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().isActionJustPressed(LingoInputActions.MENU_UP)) {
            selectedRow = (selectedRow + 2) % 3;
        }
        if (context.getInputManager().isActionJustPressed(LingoInputActions.MENU_DOWN)) {
            selectedRow = (selectedRow + 1) % 3;
        }
        if (context.getInputManager().isActionJustPressed(LingoInputActions.MENU_LEFT)) {
            adjustSelectedVolume(-VOLUME_STEP);
        }
        if (context.getInputManager().isActionJustPressed(LingoInputActions.MENU_RIGHT)) {
            adjustSelectedVolume(VOLUME_STEP);
        }
        if (context.getInputManager().isActionJustPressed(LingoInputActions.GAME_MENU)
            || context.getInputManager().isActionJustPressed(LingoInputActions.MENU_CONFIRM)) {
            context.getSceneManager().setActiveScene(LingoSession.get().getSettingsReturnSceneId());
        }
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render() {
        context.getOutputManager().clearScreen(BACKGROUND.r, BACKGROUND.g, BACKGROUND.b, BACKGROUND.a);
        context.getOutputManager().drawPanel(76f, 84f, 488f, 304f, PANEL_FILL, PANEL_BORDER);
        context.getOutputManager().drawTextCenteredWithShadow("SETTINGS", 320f, 358f, TEXT_PRIMARY);
        context.getOutputManager().drawTextCentered("Adjust master, music, and game sound volumes", 320f, 332f, TEXT_MUTED);

        AudioManager audio = context.getAudioManager();
        drawVolumeRow(0, "Master Volume", audio.getMasterVolume(), 278f);
        drawVolumeRow(1, "Music Volume", audio.getMusicVolume(), 224f);
        drawVolumeRow(2, "Game Sounds", audio.getSoundMasterVolume(), 170f);

        context.getOutputManager().drawTextCentered("A/D or LEFT/RIGHT: adjust    W/S or UP/DOWN: select", 320f, 116f, TEXT_MUTED);
        context.getOutputManager().drawTextCentered("ENTER, M or ESC: back", 320f, 94f, TEXT_MUTED);
    }

    @Override
    public void dispose() {
    }

    private void drawVolumeRow(int rowIndex, String label, float volume, float y) {
        boolean selected = rowIndex == selectedRow;
        Color fill = selected ? ROW_SELECTED_FILL : ROW_FILL;
        Color border = selected ? ROW_SELECTED_BORDER : ROW_BORDER;
        Color labelColor = selected ? TEXT_DARK : TEXT_PRIMARY;
        Color valueColor = selected ? TEXT_DARK : TEXT_MUTED;

        context.getOutputManager().drawPanel(108f, y, 424f, 36f, fill, border);
        context.getOutputManager().drawText(label, 130f, y + 24f, labelColor);
        context.getOutputManager().drawTextRightAligned(Math.round(volume * 100f) + "%", 506f, y + 24f, valueColor);
        context.getOutputManager().drawRect(272f, y + 11f, 188f, 8f, BAR_BACKGROUND);
        context.getOutputManager().drawRect(272f, y + 11f, 188f * MathUtils.clamp(volume, 0f, 1f), 8f, BAR_FILL);
    }

    private void adjustSelectedVolume(float delta) {
        AudioManager audio = context.getAudioManager();
        switch (selectedRow) {
            case 0 -> audio.setMasterVolume(audio.getMasterVolume() + delta);
            case 1 -> audio.setMusicVolume(audio.getMusicVolume() + delta);
            case 2 -> audio.setSoundMasterVolume(audio.getSoundMasterVolume() + delta);
            default -> {
            }
        }
    }
}
