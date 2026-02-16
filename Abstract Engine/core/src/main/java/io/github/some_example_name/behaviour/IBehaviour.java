package io.github.some_example_name.behaviour;

public interface IBehaviour {
    String getBehaviourId();
    void enter();
    void update(float deltaTime);
    void exit();
}
