package io.github.some_example_name.lingoman.movement;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.lingoman.level.MazeLayout;
import io.github.some_example_name.movement.Movable;
import io.github.some_example_name.movement.behaviour.MovementBehaviour;

abstract class AbstractMazeBehaviour extends MovementBehaviour {

    private static final float AXIS_CORRECTION_FACTOR = 6f;
    private static final float MAX_AXIS_CORRECTION_SHARE = 0.35f;

    protected final MazeLayout.Layout layout;
    protected final float speed;

    protected AbstractMazeBehaviour(MazeLayout.Layout layout, float speed) {
        if (layout == null) {
            throw new IllegalArgumentException("layout cannot be null");
        }
        this.layout = layout;
        this.speed = Math.max(0f, speed);
    }

    protected GridPoint2 currentCell(Movable entity) {
        return MazeLayout.toCell(layout, entity.getPosition().x, entity.getPosition().y, entitySize(entity));
    }

    protected GridPoint2 targetCell(Movable entity) {
        return MazeLayout.toCell(layout, entity.getPosition().x, entity.getPosition().y, entitySize(entity));
    }

    protected void moveToCell(Movable entity, GridPoint2 cell) {
        if (entity == null || cell == null) {
            if (entity != null) {
                entity.setVelocity(new Vector2());
            }
            return;
        }

        Vector2 entityCenter = entityCenter(entity);
        Vector2 targetCenter = MazeLayout.toCellCenter(layout, cell);
        Vector2 direction = targetCenter.sub(entityCenter);
        if (direction.len2() <= 1f) {
            entity.setVelocity(new Vector2());
            return;
        }

        GridPoint2 current = currentCell(entity);
        int dx = cell.x - current.x;
        int dy = cell.y - current.y;

        Vector2 velocity;
        if (Math.abs(dx) + Math.abs(dy) == 1) {
            velocity = axisAlignedVelocity(direction, dx, dy);
        } else {
            velocity = direction.nor().scl(speed);
        }

        entity.setVelocity(velocity);
    }

    protected boolean isNearCell(Movable entity, GridPoint2 cell, float threshold) {
        if (entity == null || cell == null) {
            return false;
        }

        Vector2 entityCenter = entityCenter(entity);
        Vector2 targetCenter = MazeLayout.toCellCenter(layout, cell);
        return entityCenter.dst2(targetCenter) <= threshold * threshold;
    }

    protected GridPoint2 nextStepTowards(Movable entity, GridPoint2 goal) {
        if (entity == null || goal == null) {
            return null;
        }

        GridPoint2 current = currentCell(entity);
        if (current.equals(goal)) {
            return new GridPoint2(goal);
        }

        GridPoint2 nextStep = MazeLayout.findNextStepTowards(layout, current, goal);
        return nextStep == null ? null : new GridPoint2(nextStep);
    }

    protected float entitySize(Movable entity) {
        if (entity instanceof Entity gameEntity) {
            return Math.max(gameEntity.getWidth(), gameEntity.getHeight());
        }
        return layout.getTileSize() * 0.7f;
    }

    private Vector2 entityCenter(Movable entity) {
        Vector2 position = entity.getPosition();
        float size = entitySize(entity);
        return new Vector2(position.x + size * 0.5f, position.y + size * 0.5f);
    }

    private Vector2 axisAlignedVelocity(Vector2 direction, int dx, int dy) {
        float correctionLimit = speed * MAX_AXIS_CORRECTION_SHARE;
        if (dx != 0) {
            float correctionY = clamp(direction.y * AXIS_CORRECTION_FACTOR, -correctionLimit, correctionLimit);
            return clampSpeed(new Vector2(Math.signum(dx) * speed, correctionY));
        }

        if (dy != 0) {
            float correctionX = clamp(direction.x * AXIS_CORRECTION_FACTOR, -correctionLimit, correctionLimit);
            return clampSpeed(new Vector2(correctionX, -Math.signum(dy) * speed));
        }

        return direction.nor().scl(speed);
    }

    private Vector2 clampSpeed(Vector2 velocity) {
        if (velocity.len2() > speed * speed) {
            velocity.nor().scl(speed);
        }
        return velocity;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
