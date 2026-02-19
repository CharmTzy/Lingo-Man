package io.github.some_example_name.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.movement.behaviour.MovementBehaviour;

/**
 * Marks an object as movable by the MovementManager.
 *
 * <p>This interface is deliberately minimal and engine-generic.
 * Concrete games/simulations can implement it on any entity type.
 */
public interface Movable {

    Vector2 getPosition();
    void setPosition(Vector2 position);

    Vector2 getVelocity();
    void setVelocity(Vector2 velocity);

    MovementBehaviour getMovementBehaviour();
    void setMovementBehaviour(MovementBehaviour behaviour);
}
