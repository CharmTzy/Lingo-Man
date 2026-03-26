package io.github.some_example_name.lingoman.movement;

import io.github.some_example_name.lingoman.entity.GhostEntity;
import io.github.some_example_name.movement.Movable;
import io.github.some_example_name.movement.behaviour.MovementBehaviour;

/**
 * A game-layer decorator that wraps any {@link MovementBehaviour} and guards it
 * against being invoked while the owning {@link GhostEntity} is frozen.
 *
 * <h3>Why this exists</h3>
 * When ghosts are registered with the {@code MovementManager} (physics disabled),
 * the manager calls {@code behaviour.move()} every frame regardless of game state.
 * Without this wrapper the underlying maze behaviour would overwrite the zeroed
 * velocity set by the freeze mechanic, allowing ghosts to slide while visually
 * frozen.
 *
 * <h3>OCP compliance</h3>
 * Neither {@code MovementManager} nor any engine class was changed to accommodate
 * frozen ghosts. The freeze guard lives entirely in this game-layer class.
 */
public final class FrozenAwareBehaviour extends MovementBehaviour {

    private final GhostEntity ghost;
    private final MovementBehaviour delegate;

    /**
     * @param ghost    the ghost whose frozen state is checked each frame
     * @param delegate the underlying behaviour to delegate to when not frozen
     */
    public FrozenAwareBehaviour(GhostEntity ghost, MovementBehaviour delegate) {
        if (ghost == null) throw new IllegalArgumentException("ghost cannot be null");
        if (delegate == null) throw new IllegalArgumentException("delegate cannot be null");
        this.ghost = ghost;
        this.delegate = delegate;
    }

    /**
     * Delegates to the underlying behaviour only when the ghost is not frozen.
     * When frozen the method returns immediately, preserving the zero-velocity
     * state set by {@link GhostEntity#freezeFor}.
     */
    @Override
    public void move(Movable entity, float deltaTime) {
        if (ghost.isFrozen()) {
            return;
        }
        delegate.move(entity, deltaTime);
    }
}
