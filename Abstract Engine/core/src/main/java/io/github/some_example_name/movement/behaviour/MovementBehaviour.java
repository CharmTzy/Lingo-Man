package io.github.some_example_name.movement.behaviour;

import io.github.some_example_name.movement.Movable;

/**
 * Abstract movement strategy.
 *
 * <p>This is part of the Abstract Engine: it defines <i>how</i> movement decisions are
 * expressed, but not any game-specific rules.
 */
public abstract class MovementBehaviour {

    /**
     * Update the entity's velocity/direction/state for this frame.
     * Position integration is handled separately by MovementPhysics.
     */
    public abstract void move(Movable entity, float deltaTime);
}
