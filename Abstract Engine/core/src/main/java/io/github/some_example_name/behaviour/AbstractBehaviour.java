package io.github.some_example_name.behaviour;

public abstract class AbstractBehaviour implements IBehaviour {

    private final String behaviourId;

    protected AbstractBehaviour(String behaviourId) {
        this.behaviourId = behaviourId;
    }

    @Override
    public String getBehaviourId() {
        return behaviourId;
    }

    @Override
    public void enter() { }

    @Override
    public void exit() { }
}
