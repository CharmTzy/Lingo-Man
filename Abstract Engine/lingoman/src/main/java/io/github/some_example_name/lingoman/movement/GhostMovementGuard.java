package io.github.some_example_name.lingoman.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lingoman.entity.GhostEntity;
import io.github.some_example_name.movement.Movable;
import io.github.some_example_name.movement.behaviour.MovementBehaviour;

public final class GhostMovementGuard extends MovementBehaviour {

    private final MovementBehaviour delegate;

    public GhostMovementGuard(MovementBehaviour delegate) {
        this.delegate = delegate;
    }

    @Override
    public void move(Movable entity, float deltaTime) {
        if (entity == null) {
            return;
        }
        if (entity instanceof GhostEntity ghost && ghost.isFrozen()) {
            entity.setVelocity(new Vector2());
            return;
        }
        if (delegate != null) {
            delegate.move(entity, deltaTime);
        } else {
            entity.setVelocity(new Vector2());
        }
    }
}