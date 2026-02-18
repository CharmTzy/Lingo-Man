package io.github.some_example_name.lwjgl3.engine.scene;

public final class SceneManager {
    private IScene current;

    public void setScene(IScene next) {
        if (current != null) current.onExit();
        current = next;
        if (current != null) current.onEnter();
    }

    public IScene getCurrent() {
        return current;
    }
}
