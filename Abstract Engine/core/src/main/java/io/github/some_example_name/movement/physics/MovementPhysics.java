package io.github.some_example_name.movement.physics;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.movement.Movable;

/**
 * Movement physics/integration helper.
 *
 * <p>All movement calculations live here (dirty work), not in the MovementManager.
 * This class is engine-generic and can be configured/extended.
 */
public class MovementPhysics {

    private float maxSpeed = Float.POSITIVE_INFINITY;
    private float linearDampingPerSecond = 0f; // 0 = no damping

    /** Integrates position from velocity. */
    public void integrate(Movable entity, float deltaTime) {
        if (entity == null) return;

        Vector2 pos = entity.getPosition();
        Vector2 vel = entity.getVelocity();
        if (pos == null) pos = new Vector2();
        if (vel == null) vel = new Vector2();

        // Clamp speed if configured
        if (Float.isFinite(maxSpeed) && vel.len2() > maxSpeed * maxSpeed) {
            vel.nor().scl(maxSpeed);
        }

        // Apply linear damping (very simple)
        if (linearDampingPerSecond > 0f) {
            float damp = Math.max(0f, 1f - linearDampingPerSecond * deltaTime);
            vel.scl(damp);
        }

        // Integrate
        pos.mulAdd(vel, deltaTime);

        entity.setVelocity(vel);
        entity.setPosition(pos);
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed <= 0 ? 0 : maxSpeed;
    }

    public float getLinearDampingPerSecond() {
        return linearDampingPerSecond;
    }

    public void setLinearDampingPerSecond(float linearDampingPerSecond) {
        this.linearDampingPerSecond = Math.max(0f, linearDampingPerSecond);
    }
}
