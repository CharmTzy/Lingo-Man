package io.github.some_example_name.collision;

import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.entity.Entity;

/**
 * A simple axis-aligned bounding box collider.
 */
public class Collider {

    private final Rectangle bounds = new Rectangle();

    private ICollisionListener listener;
    private final Entity owner;
    private float width;
    private float height;

    public Collider(Entity owner, float width, float height) {
        if (owner == null) {
            throw new IllegalArgumentException("owner cannot be null");
        }
        this.owner = owner;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the current world-space bounds for this collider.
     *
     * <p>Returned rectangle is a cached instance, do not store it.
     */
    public Rectangle getBounds() {
        float w = width > 0 ? width : owner.getWidth();
        float h = height > 0 ? height : owner.getHeight();
        bounds.set(owner.getX(), owner.getY(), w, h);
        return bounds;
    }

    public ICollisionListener getListener() {
        return listener;
    }

    public void setListener(ICollisionListener listener) {
        this.listener = listener;
    }

    public Entity getOwner() {
        return owner;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
}
