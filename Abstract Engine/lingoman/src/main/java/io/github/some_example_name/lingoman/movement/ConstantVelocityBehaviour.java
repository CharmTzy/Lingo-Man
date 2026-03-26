package io.github.some_example_name.lingoman.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.movement.Movable;
import io.github.some_example_name.movement.behaviour.MovementBehaviour;

/**
 * A game-layer {@link MovementBehaviour} that maintains a fixed velocity on its
 * entity every frame.
 *
 * <h3>Purpose</h3>
 * Used by {@code BossFireballEntity} to participate in the {@code MovementManager}
 * pipeline while preserving straight-line, constant-speed travel. The behaviour
 * re-applies the original velocity each frame so that {@link
 * io.github.some_example_name.movement.physics.MovementPhysics#integrate} can
 * handle position stepping — removing the need for the entity to self-integrate.
 *
 * <h3>Why re-apply each frame?</h3>
 * {@code MovementPhysics} may apply damping or clamping after the behaviour runs.
 * By re-setting the target velocity every frame the fireball always travels at
 * exactly the intended speed, regardless of physics configuration.
 */
public final class ConstantVelocityBehaviour extends MovementBehaviour {

    private final float vx;
    private final float vy;

    /**
     * @param vx constant x-component of velocity (pixels/units per second)
     * @param vy constant y-component of velocity (pixels/units per second)
     */
    public ConstantVelocityBehaviour(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }

    /**
     * Resets the entity's velocity to the fixed values supplied at construction.
     * {@code MovementPhysics.integrate()} will then step the position forward.
     */
    @Override
    public void move(Movable entity, float deltaTime) {
        if (entity == null) return;
        entity.setVelocity(new Vector2(vx, vy));
    }
}
