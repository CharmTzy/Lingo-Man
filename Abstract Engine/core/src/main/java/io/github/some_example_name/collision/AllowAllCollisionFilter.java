package io.github.some_example_name.collision;

/** Default filter that allows every pair to be tested. */
public final class AllowAllCollisionFilter implements ICollisionFilter {
    @Override
    public boolean canCollide(Collider a, Collider b) {
        return true;
    }
}
