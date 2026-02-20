package io.github.some_example_name.scenes;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.managers.AudioManager;

public class GameOverScene implements Scene {

    private EngineContext context;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
    }

    @Override
    public void enter() {
        context.getAudioManager().playMusic(AudioManager.BGM_GAME_OVER, true);
        System.out.println("[GameOverScene] Game Over - R to restart, M for menu");
    }

    @Override
    public void exit() {
        System.out.println("[GameOverScene] Leaving game over screen");
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().isRestartJustPressed()) {
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
        context.getOutputManager().clearScreen(0.20f, 0.05f, 0.05f, 1f);
        float musicVolume = context.getAudioManager().getMusicVolume() * 100f;
        String musicStatus = context.getAudioManager().isMuted() ? "Muted" : Math.round(musicVolume) + "%";

        context.getOutputManager().drawText("GAME OVER", 220f, 360f);
        context.getOutputManager().drawText("R: Restart   M: Menu", 180f, 300f);
        context.getOutputManager().drawText("Music: " + musicStatus, 210f, 240f);
        context.getOutputManager().drawText("Volume: - / =   Mute: V", 170f, 210f);
    }

    @Override
    public void dispose() {
        System.out.println("[GameOverScene] Resources disposed");
    }
}
