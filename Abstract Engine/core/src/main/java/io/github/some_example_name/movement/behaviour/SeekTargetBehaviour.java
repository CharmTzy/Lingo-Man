package io.github.some_example_name.movement.behaviour;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.movement.Movable;

/**
 * Generic seek behaviour: moves the entity toward a target Movable.
 *
 * <p>This stays abstract-engine friendly because it seeks a generic target, not a "player".
 */
public class SeekTargetBehaviour extends MovementBehaviour {

    private final Movable target;
    private final float speed;
    private final float stopDistance;

    public SeekTargetBehaviour(Movable target, float speed, float stopDistance) {
        if (target == null) throw new IllegalArgumentException("target cannot be null");
        this.target = target;
        this.speed = Math.max(0f, speed);
        this.stopDistance = Math.max(0f, stopDistance);
    }

    @Override
    public void move(Movable entity, float deltaTime) {
        Vector2 pos = entity.getPosition();
        Vector2 tgt = target.getPosition();
        if (pos == null || tgt == null) {
            entity.setVelocity(new Vector2());
            return;
        }

        Vector2 toTarget = new Vector2(tgt).sub(pos);
        float dist = toTarget.len();
        if (dist <= stopDistance) {
            entity.setVelocity(new Vector2());
            return;
        }
        entity.setVelocity(toTarget.nor().scl(speed));
    }
}
