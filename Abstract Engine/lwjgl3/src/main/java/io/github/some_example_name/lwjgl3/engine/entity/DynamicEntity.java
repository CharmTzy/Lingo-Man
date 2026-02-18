package io.github.some_example_name.entities;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.movement.Movable;
import io.github.some_example_name.movement.behaviour.MovementBehaviour;

/**
 * Base class for entities that can move.
 *
 * <p>Abstract: concrete games should extend this (e.g., NPCEntity, PlayerEntity, etc.).
 */
public abstract class DynamicEntity extends Entity implements Movable {

    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();
    private MovementBehaviour movementBehaviour;

    protected DynamicEntity(String id) {
        super(id);
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector2 position) {
        this.position = (position == null) ? new Vector2() : position;
    }

    @Override
    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector2 velocity) {
        this.velocity = (velocity == null) ? new Vector2() : velocity;
    }

    @Override
    public MovementBehaviour getMovementBehaviour() {
        return movementBehaviour;
    }

    @Override
    public void setMovementBehaviour(MovementBehaviour behaviour) {
        this.movementBehaviour = behaviour;
    }
}
