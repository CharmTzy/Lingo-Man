package io.github.some_example_name.scenes;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.managers.AudioManager;

public class PauseScene implements Scene {

    private EngineContext context;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
    }

    @Override
    public void enter() {
        context.getAudioManager().playMusic(AudioManager.BGM_PAUSE, true);
        System.out.println("[PauseScene] Game paused - ESC to resume, M for menu");
    }

    @Override
    public void exit() {
        System.out.println("[PauseScene] Unpausing");
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().isPauseJustPressed()) {
            context.getAudioManager().playSound(AudioManager.SFX_MENU_NAVIGATE, false);
            context.getSceneManager().setActiveScene(SceneId.GAME);
        }
        if (context.getInputManager().isMenuJustPressed()) {
            context.getAudioManager().playSound(AudioManager.SFX_MENU_NAVIGATE, false);
            context.getSceneManager().setActiveScene(SceneId.MENU);
        }
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render() {
        context.getOutputManager().clearScreen(0.15f, 0.10f, 0.05f, 1f);
    }

    @Override
    public void dispose() {
        System.out.println("[PauseScene] Resources disposed");
    }
}
