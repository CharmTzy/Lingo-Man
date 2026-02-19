package io.github.some_example_name.collision;

/**
 * Collision resolution policy.
 *
 * <p>Separates detection (CollisionManager) from positional resolution.
 * This supports SRP and makes resolution extendable.
 */
public interface ICollisionResolver {
    void resolve(Collider a, Collider b);
}
