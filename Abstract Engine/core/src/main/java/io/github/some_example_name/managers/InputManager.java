package io.github.some_example_name.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract Engine Input Manager.
 * Handles user input via action mappings so game code can stay input-device agnostic.
 */
public class InputManager {

    private final Map<String, Set<Integer>> keyBindings = new LinkedHashMap<>();

    public void bindAction(String action, int... keys) {
        validateAction(action);
        if (keys == null || keys.length == 0) {
            throw new IllegalArgumentException("At least one key is required for action: " + action);
        }

        Set<Integer> normalized = new LinkedHashSet<>();
        for (int key : keys) {
            if (key >= 0) {
                normalized.add(key);
            }
        }
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("No valid keys provided for action: " + action);
        }
        keyBindings.put(action, normalized);
    }

    public void addKeyBinding(String action, int key) {
        validateAction(action);
        if (key < 0) {
            return;
        }
        keyBindings.computeIfAbsent(action, ignored -> new LinkedHashSet<>()).add(key);
    }

    public void clearAction(String action) {
        if (action == null) {
            return;
        }
        keyBindings.remove(action);
    }

    public boolean isActionPressed(String action) {
        Set<Integer> keys = keyBindings.get(action);
        if (keys == null || keys.isEmpty()) {
            return false;
        }
        for (int key : keys) {
            if (Gdx.input.isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean isActionJustPressed(String action) {
        Set<Integer> keys = keyBindings.get(action);
        if (keys == null || keys.isEmpty()) {
            return false;
        }
        for (int key : keys) {
            if (Gdx.input.isKeyJustPressed(key)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Set<Integer>> getBindings() {
        Map<String, Set<Integer>> snapshot = new LinkedHashMap<>();
        for (Map.Entry<String, Set<Integer>> entry : keyBindings.entrySet()) {
            snapshot.put(entry.getKey(), Collections.unmodifiableSet(new LinkedHashSet<>(entry.getValue())));
        }
        return Collections.unmodifiableMap(snapshot);
    }

    /** Returns the current mouse position in screen coordinates. */
    public Vector2 getMousePosition() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }

    /** Returns true if the mouse is clicked. */
    public boolean isMouseClicked() {
        return Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    private void validateAction(String action) {
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Action id cannot be null or blank.");
        }
    }

}
