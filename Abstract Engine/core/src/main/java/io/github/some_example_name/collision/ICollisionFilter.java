package io.github.some_example_name.collision;

/**
 * Collision filtering policy (Open/Closed).
 *
 * <p>Implementations decide whether two colliders are allowed to collide.
 * This keeps CollisionManager closed for modification when rules change.
 */
public interface ICollisionFilter {
    boolean canCollide(Collider a, Collider b);
}
