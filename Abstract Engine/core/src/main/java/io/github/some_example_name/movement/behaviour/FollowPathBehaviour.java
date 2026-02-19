package io.github.some_example_name.movement.behaviour;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.movement.Movable;

/**
 * Generic path following behaviour: similar to patrol, but can be configured to stop at the end.
 */
public class FollowPathBehaviour extends MovementBehaviour {

    private final List<Vector2> path;
    private final float speed;
    private final float arriveThreshold;
    private final boolean loop;
    private int index = 0;

    public FollowPathBehaviour(List<Vector2> path, float speed, float arriveThreshold, boolean loop) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path must be non-empty");
        }
        this.path = path;
        this.speed = Math.max(0f, speed);
        this.arriveThreshold = Math.max(0.001f, arriveThreshold);
        this.loop = loop;
    }

    @Override
    public void move(Movable entity, float deltaTime) {
        if (index >= path.size()) {
            entity.setVelocity(new Vector2());
            return;
        }

        Vector2 pos = entity.getPosition();
        if (pos == null) pos = new Vector2();

        Vector2 target = path.get(index);
        Vector2 toTarget = new Vector2(target).sub(pos);

        if (toTarget.len() <= arriveThreshold) {
            index++;
            if (loop && index >= path.size()) {
                index = 0;
            }
            entity.setVelocity(new Vector2());
            return;
        }

        entity.setVelocity(toTarget.nor().scl(speed));
    }
}
