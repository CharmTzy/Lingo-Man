package io.github.some_example_name.entity;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.movement.Movable;
import io.github.some_example_name.movement.behaviour.MovementBehaviour;

/**
 * Base class for entities that can move.
 *
 * <p>Abstract: concrete games should extend this (e.g., NPCEntity, PlayerEntity, etc.).
 */
public abstract class DynamicEntity extends Entity implements Movable {

    private MovementBehaviour movementBehaviour;
    private final Vector2 positionCache = new Vector2();
    private final Vector2 velocityCache = new Vector2();

    protected DynamicEntity(String id) {
        super(id);
    }

    @Override
    public Vector2 getPosition() {
        return positionCache.set(getX(), getY());
    }

    @Override
    public void setPosition(Vector2 position) {
        if (position == null) {
            setX(0f);
            setY(0f);
            return;
        }
        setX(position.x);
        setY(position.y);
    }

    @Override
    public Vector2 getVelocity() {
        return velocityCache.set(getVx(), getVy());
    }

    @Override
    public void setVelocity(Vector2 velocity) {
        if (velocity == null) {
            setVx(0f);
            setVy(0f);
            return;
        }
        setVx(velocity.x);
        setVy(velocity.y);
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
