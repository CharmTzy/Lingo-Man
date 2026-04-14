package io.github.some_example_name.lingoman.scenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.lingoman.LingoAudio;
import io.github.some_example_name.lingoman.LingoInputActions;
import io.github.some_example_name.lingoman.LingoSceneIds;
import io.github.some_example_name.lingoman.LingoSaveFiles;
import io.github.some_example_name.lingoman.LingoSession;
import io.github.some_example_name.lingoman.model.GameState;
import io.github.some_example_name.save.ISaveable;
import io.github.some_example_name.save.SaveData;
import io.github.some_example_name.scenes.Scene;

public class MenuScene implements Scene, ISaveable {

    private static final float OPTION_X = 132f;
    private static final float OPTION_WIDTH = 376f;
    private static final float OPTION_HEIGHT = 32f;
    private static final float OPTION_TOP_Y = 268f;
    private static final float OPTION_GAP = 40f;

    private static final Color BACKGROUND = new Color(0.02f, 0.05f, 0.14f, 1f);
    private static final Color BACKDROP_TOP = new Color(0.05f, 0.19f, 0.36f, 0.52f);
    private static final Color BACKDROP_BOTTOM = new Color(0.03f, 0.10f, 0.24f, 0.48f);
    private static final Color BACKDROP_STRIPE = new Color(1.00f, 0.78f, 0.24f, 0.24f);
    private static final Color CARD_FILL = new Color(0.03f, 0.10f, 0.20f, 0.96f);
    private static final Color CARD_BORDER = new Color(0.32f, 0.90f, 1.00f, 1f);
    private static final Color OPTION_FILL = new Color(0.08f, 0.18f, 0.30f, 0.98f);
    private static final Color OPTION_BORDER = new Color(0.27f, 0.52f, 0.67f, 1f);
    private static final Color OPTION_SELECTED_FILL = new Color(0.24f, 0.84f, 1.00f, 1f);
    private static final Color OPTION_SELECTED_BORDER = new Color(0.88f, 0.98f, 1.00f, 1f);
    private static final Color TEXT_PRIMARY = new Color(0.95f, 0.98f, 1.00f, 1f);
    private static final Color TEXT_MUTED = new Color(0.72f, 0.88f, 0.96f, 1f);
    private static final Color TEXT_DARK = new Color(0.02f, 0.11f, 0.20f, 1f);

    private EngineContext context;
    private final MenuOption[] options = {
        new MenuOption("Easy", "GhastlyGhost", GameState.Difficulty.EASY, null),
        new MenuOption("Medium", "GhastlyGhost + MadMortar", GameState.Difficulty.MEDIUM, null),
        new MenuOption("Hard", "FireLord + HellHound + MadMortar", GameState.Difficulty.HARD, null),
        new MenuOption("Settings", "audio levels", null, LingoSceneIds.SETTINGS),
        new MenuOption("Words Found", "open list", null, LingoSceneIds.FOUND_WORDS),
        new MenuOption("Quit", "Exit game", null, null)
    };
    private int selectedIndex = 0;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
        context.getSaveManager().register(this);
        if (context.getSaveManager().hasSaveFile(LingoSaveFiles.PROFILE)) {
            context.getSaveManager().load(LingoSaveFiles.PROFILE);
        }
    }

    @Override
    public void enter() {
        context.getAudioManager().playMusic(LingoAudio.BGM_MENU, true);
        System.out.println("[LingoMan] Menu entered");
    }

    @Override
    public void exit() {
        System.out.println("[LingoMan] Menu exit");
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().isActionJustPressed(LingoInputActions.MENU_UP)) {
            selectedIndex = (selectedIndex - 1 + options.length) % options.length;
            context.getAudioManager().playSound(LingoAudio.SFX_MENU_NAVIGATE, false);
        }
        if (context.getInputManager().isActionJustPressed(LingoInputActions.MENU_DOWN)) {
            selectedIndex = (selectedIndex + 1) % options.length;
            context.getAudioManager().playSound(LingoAudio.SFX_MENU_NAVIGATE, false);
        }
        if (context.getInputManager().isActionJustPressed(LingoInputActions.MENU_CONFIRM)) {
            activateOption(selectedIndex);
        }

        int clickedIndex = getClickedOptionIndex();
        if (clickedIndex >= 0) {
            selectedIndex = clickedIndex;
            context.getAudioManager().playSound(LingoAudio.SFX_MENU_NAVIGATE, false);
            activateOption(clickedIndex);
        }
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render() {
        context.getOutputManager().clearScreen(BACKGROUND.r, BACKGROUND.g, BACKGROUND.b, BACKGROUND.a);
        context.getOutputManager().drawRect(0f, 382f, 640f, 98f, BACKDROP_TOP);
        context.getOutputManager().drawRect(0f, 0f, 640f, 120f, BACKDROP_BOTTOM);
        context.getOutputManager().drawRect(44f, 336f, 552f, 10f, BACKDROP_STRIPE);
        context.getOutputManager().drawRect(44f, 110f, 552f, 10f, BACKDROP_STRIPE);
        context.getOutputManager().drawPanel(88f, 30f, 464f, 386f, CARD_FILL, CARD_BORDER);

        context.getOutputManager().drawTextCenteredWithShadow("SELECT MODE", 320f, 378f, TEXT_PRIMARY);
        context.getOutputManager().drawTextCentered("Choose difficulty, settings, or review found words", 320f, 350f, TEXT_MUTED);

        for (int i = 0; i < options.length; i++) {
            boolean selected = i == selectedIndex;
            float y = 268f - (i * 40f);
            Color fill = selected ? OPTION_SELECTED_FILL : OPTION_FILL;
            Color border = selected ? OPTION_SELECTED_BORDER : OPTION_BORDER;
            Color labelColor = selected ? TEXT_DARK : TEXT_PRIMARY;
            Color hintColor = selected ? TEXT_DARK : TEXT_MUTED;
            String hint = LingoSceneIds.FOUND_WORDS.equals(options[i].targetSceneId)
                ? LingoSession.get().getGameState().getFoundWordsCount() + " saved"
                : options[i].hint;

            context.getOutputManager().drawPanel(132f, y, 376f, 32f, fill, border);
            context.getOutputManager().drawText(options[i].label, 152f, y + 22f, labelColor);
            context.getOutputManager().drawTextRightAligned(hint, 488f, y + 22f, hintColor);
        }

        context.getOutputManager().drawTextCentered("W/S or arrow keys to move, ENTER to select", 320f, 46f, TEXT_MUTED);
    }

    @Override
    public void dispose() {
        context.getSaveManager().unregister(getSaveId());
        System.out.println("[LingoMan] Menu disposed");
    }

    @Override
    public String getSaveId() {
        return "lingoman_progress";
    }

    @Override
    public SaveData writeSaveData() {
        SaveData data = new SaveData(getSaveId());
        List<String> foundWords = LingoSession.get().getGameState().getFoundWords();
        data.put("found_words", String.join("\n", foundWords));
        return data;
    }

    @Override
    public void readSaveData(SaveData saveData) {
        if (saveData == null) {
            return;
        }

        Object rawWords = saveData.get("found_words");
        List<String> words = new ArrayList<>();
        if (rawWords instanceof String wordBlock && !wordBlock.isBlank()) {
            words.addAll(Arrays.asList(wordBlock.split("\\R")));
        }

        LingoSession.get().getGameState().setFoundWords(words);
    }

    private void activateOption(int optionIndex) {
        if (optionIndex < 0 || optionIndex >= options.length) {
            return;
        }

        MenuOption selected = options[optionIndex];
        if (selected.difficulty != null) {
            LingoSession.get().getGameState().setDifficulty(selected.difficulty);
            context.getSceneManager().setActiveScene(LingoSceneIds.GAME);
        } else if (selected.targetSceneId != null) {
            if (LingoSceneIds.SETTINGS.equals(selected.targetSceneId)) {
                LingoSession.get().setSettingsReturnSceneId(LingoSceneIds.MENU);
            }
            context.getSceneManager().setActiveScene(selected.targetSceneId);
        } else {
            Gdx.app.exit();
        }
    }

    private int getClickedOptionIndex() {
        if (!context.getInputManager().isMouseClicked()) {
            return -1;
        }

        Vector2 mouse = context.getInputManager().getMousePosition();
        Vector2 worldMouse = context.getOutputManager().screenToWorld(mouse.x, mouse.y);
        float worldX = worldMouse.x;
        float worldY = worldMouse.y;

        if (worldX < OPTION_X || worldX > OPTION_X + OPTION_WIDTH) {
            return -1;
        }

        for (int i = 0; i < options.length; i++) {
            float optionY = OPTION_TOP_Y - (i * OPTION_GAP);
            if (worldY >= optionY && worldY <= optionY + OPTION_HEIGHT) {
                return i;
            }
        }
        return -1;
    }

    private static final class MenuOption {
        final String label;
        final String hint;
        final GameState.Difficulty difficulty;
        final String targetSceneId;

        private MenuOption(String label, String hint, GameState.Difficulty difficulty, String targetSceneId) {
            this.label = label;
            this.hint = hint;
            this.difficulty = difficulty;
            this.targetSceneId = targetSceneId;
        }
    }
}
