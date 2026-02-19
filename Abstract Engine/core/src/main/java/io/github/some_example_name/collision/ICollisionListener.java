package io.github.some_example_name.collision;

/**
 * Receives collision lifecycle callbacks.
 *
 * <p>Engine responsibility (CollisionManager): detect + track collision state.
 * Listener responsibility: decide what to do (game logic, behaviour, SFX, etc.).
 */
public interface ICollisionListener {
    void onCollisionEnter(Collider other);

    void onCollisionStay(Collider other);

    void onCollisionExit(Collider other);
}
