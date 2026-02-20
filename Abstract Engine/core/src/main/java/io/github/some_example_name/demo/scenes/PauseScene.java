package io.github.some_example_name.demo.scenes;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.demo.DemoAudio;
import io.github.some_example_name.demo.DemoInputActions;
import io.github.some_example_name.demo.DemoSceneIds;
import io.github.some_example_name.scenes.Scene;

public class PauseScene implements Scene {

    private EngineContext context;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
    }

    @Override
    public void enter() {
        context.getAudioManager().playMusic(DemoAudio.BGM_PAUSE, true);
        System.out.println("[PauseScene] Game paused - ESC to resume, M for menu");
    }

    @Override
    public void exit() {
        System.out.println("[PauseScene] Unpausing");
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().isActionJustPressed(DemoInputActions.GAME_PAUSE)) {
            context.getAudioManager().playSound(DemoAudio.SFX_MENU_NAVIGATE, false);
            context.getSceneManager().setActiveScene(DemoSceneIds.GAME);
        }
        if (context.getInputManager().isActionJustPressed(DemoInputActions.GAME_MENU)) {
            context.getAudioManager().playSound(DemoAudio.SFX_MENU_NAVIGATE, false);
            context.getSceneManager().setActiveScene(DemoSceneIds.MENU);
        }
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render() {
        context.getOutputManager().clearScreen(0.15f, 0.10f, 0.05f, 1f);
        float musicVolume = context.getAudioManager().getMusicVolume() * 100f;
        String musicStatus = context.getAudioManager().isMuted() ? "Muted" : Math.round(musicVolume) + "%";

        context.getOutputManager().drawText("PAUSED", 250f, 360f);
        context.getOutputManager().drawText("ESC: Resume   M: Menu", 170f, 300f);
        context.getOutputManager().drawText("Music: " + musicStatus, 210f, 240f);
        context.getOutputManager().drawText("Volume: - / =   Mute: V", 170f, 210f);
    }

    @Override
    public void dispose() {
        System.out.println("[PauseScene] Resources disposed");
    }
}
