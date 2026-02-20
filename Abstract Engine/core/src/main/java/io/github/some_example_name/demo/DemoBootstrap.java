package io.github.some_example_name.demo;

import com.badlogic.gdx.Input;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.demo.scenes.GameOverScene;
import io.github.some_example_name.demo.scenes.GameScene;
import io.github.some_example_name.demo.scenes.MenuScene;
import io.github.some_example_name.demo.scenes.PauseScene;
import io.github.some_example_name.managers.AudioManager;
import io.github.some_example_name.managers.InputManager;
import io.github.some_example_name.managers.SceneManager;

public final class DemoBootstrap {

    private DemoBootstrap() {
    }

    public static void initialize(EngineContext context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

        configureInput(context.getInputManager());
        configureAudio(context.getAudioManager());
        configureScenes(context.getSceneManager());
    }

    private static void configureInput(InputManager input) {
        input.bindAction(DemoInputActions.MENU_UP, Input.Keys.UP, Input.Keys.W);
        input.bindAction(DemoInputActions.MENU_DOWN, Input.Keys.DOWN, Input.Keys.S);
        input.bindAction(DemoInputActions.MENU_CONFIRM, Input.Keys.ENTER);

        input.bindAction(DemoInputActions.GAME_ACTION, Input.Keys.SPACE);
        input.bindAction(DemoInputActions.GAME_PAUSE, Input.Keys.ESCAPE);
        input.bindAction(DemoInputActions.GAME_MENU, Input.Keys.M);
        input.bindAction(DemoInputActions.GAME_RESTART, Input.Keys.R);
        input.bindAction(DemoInputActions.GAME_TOGGLE_NPC_MODE, Input.Keys.TAB);
        input.bindAction(DemoInputActions.GAME_TOGGLE_COLLISION_DEBUG, Input.Keys.F1);
        input.bindAction(DemoInputActions.GAME_SAVE_SESSION, Input.Keys.F5);
        input.bindAction(DemoInputActions.GAME_LOAD_SESSION, Input.Keys.F9);
        input.bindAction(DemoInputActions.GAME_DELETE_SESSION, Input.Keys.F10);

        input.bindAction(DemoInputActions.MOVE_UP, Input.Keys.UP, Input.Keys.W);
        input.bindAction(DemoInputActions.MOVE_DOWN, Input.Keys.DOWN, Input.Keys.S);
        input.bindAction(DemoInputActions.MOVE_LEFT, Input.Keys.LEFT, Input.Keys.A);
        input.bindAction(DemoInputActions.MOVE_RIGHT, Input.Keys.RIGHT, Input.Keys.D);

        input.bindAction(DemoInputActions.AUDIO_VOLUME_UP, Input.Keys.EQUALS, Input.Keys.NUMPAD_ADD);
        input.bindAction(DemoInputActions.AUDIO_VOLUME_DOWN, Input.Keys.MINUS, Input.Keys.NUMPAD_SUBTRACT);
        input.bindAction(DemoInputActions.AUDIO_MUTE_TOGGLE, Input.Keys.V);
    }

    private static void configureAudio(AudioManager audio) {
        audio.loadSound(DemoAudio.SFX_MENU_NAVIGATE, DemoAudio.PATH_SFX_MENU_NAVIGATE);
        audio.loadSound(DemoAudio.SFX_BORDER_COLLISION, DemoAudio.PATH_SFX_BORDER_COLLISION);
        audio.loadSound(DemoAudio.SFX_PLAYER_COLLISION, DemoAudio.PATH_SFX_PLAYER_COLLISION);

        audio.loadMusic(DemoAudio.BGM_MENU, DemoAudio.PATH_BGM_MENU);
        audio.loadMusic(DemoAudio.BGM_GAME, DemoAudio.PATH_BGM_GAME);
        audio.loadMusic(DemoAudio.BGM_PAUSE, DemoAudio.PATH_BGM_PAUSE);
        audio.loadMusic(DemoAudio.BGM_GAME_OVER, DemoAudio.PATH_BGM_GAME_OVER);
    }

    private static void configureScenes(SceneManager sceneManager) {
        sceneManager.configureGlobalAudioActions(
            DemoInputActions.AUDIO_MUTE_TOGGLE,
            DemoInputActions.AUDIO_VOLUME_UP,
            DemoInputActions.AUDIO_VOLUME_DOWN
        );

        sceneManager.registerScene(DemoSceneIds.MENU, new MenuScene());
        sceneManager.registerScene(DemoSceneIds.GAME, new GameScene());
        sceneManager.registerScene(DemoSceneIds.PAUSE, new PauseScene());
        sceneManager.registerScene(DemoSceneIds.GAME_OVER, new GameOverScene());
        sceneManager.setActiveScene(DemoSceneIds.MENU);
    }
}
