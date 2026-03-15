package io.github.some_example_name.lingoman.movement;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import io.github.some_example_name.lingoman.level.MazeLayout;
import io.github.some_example_name.movement.Movable;

public final class MazePatrolBehaviour extends AbstractMazeBehaviour {

    private final List<GridPoint2> waypoints;
    private final float arriveThreshold;
    private int index = 0;
    private GridPoint2 currentStep;

    public MazePatrolBehaviour(MazeLayout.Layout layout, List<GridPoint2> waypoints, float speed, float arriveThreshold) {
        super(layout, speed);
        if (waypoints == null || waypoints.isEmpty()) {
            throw new IllegalArgumentException("waypoints must be non-empty");
        }
        this.waypoints = new ArrayList<>(waypoints);
        this.arriveThreshold = Math.max(1f, arriveThreshold);
    }

    @Override
    public void move(Movable entity, float deltaTime) {
        GridPoint2 target = waypoints.get(index);
        if (isNearCell(entity, target, arriveThreshold)) {
            index = (index + 1) % waypoints.size();
            target = waypoints.get(index);
            currentStep = null;
        }

        if (currentStep == null || isNearCell(entity, currentStep, arriveThreshold)) {
            currentStep = nextStepTowards(entity, target);
        }

        moveToCell(entity, currentStep == null ? target : currentStep);
    }
}
