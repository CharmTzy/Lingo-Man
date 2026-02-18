package io.github.some_example_name.managers;

import io.github.some_example_name.behaviour.IBehaviour;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BehaviourManager {

    private final Map<String, IBehaviour> behaviours;

    private IBehaviour activeBehaviour;

    public BehaviourManager() {
        this.behaviours = new HashMap<>();
    }

    public void register(IBehaviour behaviour) {
        String id = behaviour.getBehaviourId();
        if (behaviours.containsKey(id)) {
            throw new IllegalArgumentException(
                "A behaviour with id '" + id + "' is already registered.");
        }
        behaviours.put(id, behaviour);
    }

    public void unregister(String behaviourId) {
        if (activeBehaviour != null
            && activeBehaviour.getBehaviourId().equals(behaviourId)) {
            activeBehaviour.exit();
            activeBehaviour = null;
        }
        behaviours.remove(behaviourId);
    }

    public void setActiveBehaviour(String behaviourId) {
        IBehaviour next = behaviours.get(behaviourId);
        if (next == null) {
            throw new IllegalArgumentException(
                "No behaviour with id '" + behaviourId + "' is registered.");
        }
        if (activeBehaviour != null) {
            activeBehaviour.exit();
        }
        activeBehaviour = next;
        activeBehaviour.enter();
    }

    public void update(float deltaTime) {
        if (activeBehaviour != null) {
            activeBehaviour.update(deltaTime);
        }
        evaluate(deltaTime);
    }

    public IBehaviour getActiveBehaviour() {
        return activeBehaviour;
    }

    protected Map<String, IBehaviour> getBehaviours() {
        return Collections.unmodifiableMap(behaviours);
    }

    protected void evaluate(float deltaTime) {
    }
}