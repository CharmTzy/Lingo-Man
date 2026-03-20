package io.github.some_example_name.lingoman.movement;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lingoman.level.MazeLayout;
import io.github.some_example_name.movement.Movable;

public final class MazeSeekBehaviour extends AbstractMazeBehaviour {

    private final Movable target;
    private final float arriveThreshold;
    private GridPoint2 currentStep;

    public MazeSeekBehaviour(MazeLayout.Layout layout, Movable target, float speed) {
        super(layout, speed);
        if (target == null) {
            throw new IllegalArgumentException("target cannot be null");
        }
        this.target = target;
        this.arriveThreshold = Math.max(2f, layout.getTileSize() * 0.22f);
    }

    @Override
    public void move(Movable entity, float deltaTime) {
        GridPoint2 goal = targetCell(target);
        if (currentStep == null || isNearCell(entity, currentStep, arriveThreshold)) {
            currentStep = nextStepTowards(entity, goal);
        }

        if (currentStep == null) {
            Vector2 entityPos = entity.getPosition();
            Vector2 targetPos = target.getPosition();
            Vector2 direction = new Vector2(targetPos).sub(entityPos);
            if (direction.len2() <= 1f) {
                entity.setVelocity(new Vector2());
            } else {
                entity.setVelocity(direction.nor().scl(speed));
            }
            return;
        }

        moveToCell(entity, currentStep);
    }
}
