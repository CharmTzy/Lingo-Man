package io.github.some_example_name;

import io.github.some_example_name.managers.AudioManager;
import io.github.some_example_name.managers.InputManager;
import io.github.some_example_name.managers.OutputManager;
import io.github.some_example_name.managers.SaveManager;
import io.github.some_example_name.managers.SceneManager;

public class EngineContext {

    private final SceneManager sceneManager;
    private final SaveManager saveManager;
    private final InputManager inputManager;
    private final OutputManager outputManager;
    private final AudioManager audioManager;

    public EngineContext() {
        this.sceneManager = new SceneManager(this);
        this.saveManager = new SaveManager();
        this.inputManager = new InputManager();
        this.outputManager = new OutputManager();
        this.audioManager = new AudioManager();
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    public InputManager getInputManager() {
        return inputManager; 
    }
    
    public OutputManager getOutputManager() {
        return outputManager; 
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }
}
