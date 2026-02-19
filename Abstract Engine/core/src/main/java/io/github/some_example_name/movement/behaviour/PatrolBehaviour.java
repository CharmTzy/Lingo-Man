package io.github.some_example_name.movement.behaviour;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.movement.Movable;

/**
 * Generic patrol behaviour: moves along a list of waypoints in order (looping).
 */
public class PatrolBehaviour extends MovementBehaviour {

    private final List<Vector2> waypoints;
    private final float speed;
    private final float arriveThreshold;
    private int index = 0;

    public PatrolBehaviour(List<Vector2> waypoints, float speed, float arriveThreshold) {
        if (waypoints == null || waypoints.isEmpty()) {
            throw new IllegalArgumentException("waypoints must be non-empty");
        }
        this.waypoints = waypoints;
        this.speed = Math.max(0f, speed);
        this.arriveThreshold = Math.max(0.001f, arriveThreshold);
    }

    @Override
    public void move(Movable entity, float deltaTime) {
        Vector2 pos = entity.getPosition();
        if (pos == null) pos = new Vector2();

        Vector2 target = waypoints.get(index);
        Vector2 toTarget = new Vector2(target).sub(pos);

        if (toTarget.len() <= arriveThreshold) {
            index = (index + 1) % waypoints.size();
            target = waypoints.get(index);
            toTarget.set(target).sub(pos);
        }

        if (toTarget.len2() == 0) {
            entity.setVelocity(new Vector2());
            return;
        }

        entity.setVelocity(toTarget.nor().scl(speed));
    }
}
