package io.github.some_example_name;

import io.github.some_example_name.managers.BehaviourManager;
import io.github.some_example_name.managers.InputManager;
import io.github.some_example_name.managers.OutputManager;
import io.github.some_example_name.managers.SaveManager;
import io.github.some_example_name.managers.SceneManager;

public class EngineContext {

    private final SceneManager sceneManager;
    private final BehaviourManager behaviourManager;
    private final SaveManager saveManager;
    private final InputManager inputManager;
    private final OutputManager outputManager;

    public EngineContext() {
        this.sceneManager = new SceneManager(this);
        this.behaviourManager = new BehaviourManager();
        this.saveManager = new SaveManager();
        this.inputManager = new InputManager();
        this.outputManager = new OutputManager();
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public BehaviourManager getBehaviourManager() {
        return behaviourManager;
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
}
