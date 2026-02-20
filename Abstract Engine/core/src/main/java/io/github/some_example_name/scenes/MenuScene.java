package io.github.some_example_name.scenes;

import com.badlogic.gdx.Gdx;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.managers.AudioManager;

public class MenuScene implements Scene {

    private EngineContext context;
    private final String[] menuOptions = { "Start Game", "Quit" };
    private int selectedIndex = 0;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
    }

    @Override
    public void enter() {
        context.getAudioManager().playMusic(AudioManager.BGM_MENU, true);
        System.out.println("[MenuScene] Entered - Use UP/DOWN and ENTER");
    }

    @Override
    public void exit() {
        System.out.println("[MenuScene] Exiting menu");
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().isUpJustPressed()) {
            selectedIndex = (selectedIndex - 1 + menuOptions.length) % menuOptions.length;
            context.getAudioManager().playSound(AudioManager.SFX_MENU_NAVIGATE, false);
        }

        if (context.getInputManager().isDownJustPressed()) {
            selectedIndex = (selectedIndex + 1) % menuOptions.length;
            context.getAudioManager().playSound(AudioManager.SFX_MENU_NAVIGATE, false);
        }

        if (context.getInputManager().isEnterJustPressed()) {
            context.getAudioManager().playSound(AudioManager.SFX_MENU_NAVIGATE, false);
            if (selectedIndex == 0) {
                context.getSceneManager().setActiveScene(SceneId.GAME);
            } else {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render() {
        context.getOutputManager().clearScreen(0.10f, 0.10f, 0.15f, 1f);
        float musicVolume = context.getAudioManager().getMusicVolume() * 100f;
        String musicStatus = context.getAudioManager().isMuted() ? "Muted" : Math.round(musicVolume) + "%";

        context.getOutputManager().drawText("ABSTRACT ENGINE", 230f, 420f);
        context.getOutputManager().drawText("Use UP / DOWN + ENTER", 190f, 360f);
        context.getOutputManager().drawText("Music: " + musicStatus, 200f, 180f);
        context.getOutputManager().drawText("Volume: - / =   Mute: V", 170f, 150f);

        for (int i = 0; i < menuOptions.length; i++) {
            String prefix = (i == selectedIndex) ? "> " : "  ";
            context.getOutputManager().drawText(prefix + menuOptions[i], 240f, 300f - (i * 40f));
        }
    }

    @Override
    public void dispose() {
        System.out.println("[MenuScene] Resources disposed");
    }
}
