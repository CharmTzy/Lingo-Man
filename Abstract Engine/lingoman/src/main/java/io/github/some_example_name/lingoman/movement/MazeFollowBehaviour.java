package io.github.some_example_name.lingoman.movement;

import java.util.ArrayDeque;
import java.util.Deque;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lingoman.level.MazeLayout;
import io.github.some_example_name.movement.Movable;

public final class MazeFollowBehaviour extends AbstractMazeBehaviour {

    private final Movable target;
    private final Deque<GridPoint2> trail = new ArrayDeque<>();
    private final int trailDelayCells;
    private final float arriveThreshold;

    private GridPoint2 lastObservedCell;
    private GridPoint2 currentStep;

    public MazeFollowBehaviour(
        MazeLayout.Layout layout,
        Movable target,
        float speed,
        int trailDelayCells,
        float arriveThreshold
    ) {
        super(layout, speed);
        if (target == null) {
            throw new IllegalArgumentException("target cannot be null");
        }
        this.target = target;
        this.trailDelayCells = Math.max(1, trailDelayCells);
        this.arriveThreshold = Math.max(1f, arriveThreshold);
    }

    @Override
    public void move(Movable entity, float deltaTime) {
        GridPoint2 current = currentCell(entity);
        GridPoint2 observedTarget = targetCell(target);
        recordTrail(observedTarget);

        GridPoint2 followCell = currentFollowCell(observedTarget);
        while (followCell != null && current.equals(followCell) && trail.size() > 1) {
            trail.removeFirst();
            followCell = currentFollowCell(observedTarget);
            currentStep = null;
        }

        if (followCell == null) {
            entity.setVelocity(new Vector2());
            return;
        }

        if (currentStep == null || isNearCell(entity, currentStep, arriveThreshold)) {
            currentStep = nextStepTowards(entity, followCell);
        }

        moveToCell(entity, currentStep == null ? followCell : currentStep);

        if (isNearCell(entity, followCell, arriveThreshold) && trail.size() > 1) {
            trail.removeFirst();
            currentStep = null;
        }
    }

    private void recordTrail(GridPoint2 observedTarget) {
        if (observedTarget == null) {
            return;
        }

        if (lastObservedCell == null || !lastObservedCell.equals(observedTarget)) {
            GridPoint2 copy = new GridPoint2(observedTarget);
            trail.addLast(copy);
            lastObservedCell = copy;
        } else if (trail.isEmpty()) {
            trail.addLast(new GridPoint2(observedTarget));
        }

        while (trail.size() > trailDelayCells + 1) {
            trail.removeFirst();
        }
    }

    private GridPoint2 currentFollowCell(GridPoint2 observedTarget) {
        if (trail.size() > trailDelayCells) {
            return trail.peekFirst();
        }
        return observedTarget;
    }
}
