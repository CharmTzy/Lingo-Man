package io.github.some_example_name.collision;

import io.github.some_example_name.entity.Entity;

/**
 * Adapts the existing {@link Entity#onCollision(Entity)} hook to the engine's
 * collision listener interface.
 */
public final class EntityCollisionListenerAdapter implements ICollisionListener {

    private final Entity owner;

    public EntityCollisionListenerAdapter(Entity owner) {
        if (owner == null) {
            throw new IllegalArgumentException("owner cannot be null");
        }
        this.owner = owner;
    }

    @Override
    public void onCollisionEnter(Collider other) {
        if (other == null) return;
        Entity otherOwner = other.getOwner();
        if (otherOwner != null) owner.onCollision(otherOwner);
    }

    @Override
    public void onCollisionStay(Collider other) {
        // Intentionally no-op by default.
        // If you want continuous effects (damage over time, friction),
        // create a different listener implementation.
    }

    @Override
    public void onCollisionExit(Collider other) {
        // No-op by default.
    }
}
