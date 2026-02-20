package io.github.some_example_name.demo.entity;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.NPCEntity;
import io.github.some_example_name.managers.OutputManager;
import io.github.some_example_name.movement.Movable;
import io.github.some_example_name.movement.behaviour.FollowPathBehaviour;
import io.github.some_example_name.movement.behaviour.PatrolBehaviour;
import io.github.some_example_name.movement.behaviour.SeekTargetBehaviour;
import io.github.some_example_name.movement.behaviour.WanderBehaviour;

public class ChasingBoxEntity extends NPCEntity {
    public enum BehaviourMode {
        FOLLOW_PATH,
        PATROL,
        SEEK_TARGET,
        WANDER;

        public BehaviourMode next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    private final Color color;
    private final Movable target;
    private final List<Vector2> route = List.of(
        new Vector2(96f, 96f),
        new Vector2(544f, 96f),
        new Vector2(544f, 384f),
        new Vector2(96f, 384f)
    );
    private BehaviourMode behaviourMode = BehaviourMode.SEEK_TARGET;

    public ChasingBoxEntity(Movable target, float x, float y) {
        super("chasing_box");
        this.color = new Color(1.00f, 0.35f, 0.30f, 1f);
        this.target = target;
        setX(x);
        setY(y);
        setWidth(32);
        setHeight(32);
        setVx(0f);
        setVy(0f);
        applyBehaviour(behaviourMode);
    }

    @Override
    public void render(OutputManager outputManager) {
        outputManager.drawRect(getX(), getY(), getWidth(), getHeight(), color);
    }

    @Override
    public void onCollision(Entity other) {
        if (other == null || !other.isActive()) {
            return;
        }
        setVx(-getVx());
        setVy(-getVy());
    }

    public void cycleBehaviourMode() {
        setBehaviourMode(behaviourMode.next());
    }

    public void setBehaviourMode(BehaviourMode mode) {
        if (mode == null) {
            return;
        }
        behaviourMode = mode;
        applyBehaviour(mode);
    }

    public BehaviourMode getBehaviourMode() {
        return behaviourMode;
    }

    public String getBehaviourModeLabel() {
        return switch (behaviourMode) {
            case FOLLOW_PATH -> "Follow";
            case PATROL -> "Patrol";
            case SEEK_TARGET -> "Seek";
            case WANDER -> "Wander";
        };
    }

    private void applyBehaviour(BehaviourMode mode) {
        switch (mode) {
            case FOLLOW_PATH:
                setMovementBehaviour(new FollowPathBehaviour(route, 95f, 6f, true));
                break;
            case PATROL:
                setMovementBehaviour(new PatrolBehaviour(route, 90f, 6f));
                break;
            case SEEK_TARGET:
                if (target != null) {
                    setMovementBehaviour(new SeekTargetBehaviour(target, 85f, 8f));
                } else {
                    setMovementBehaviour(new WanderBehaviour(75f, 0.35f));
                }
                break;
            case WANDER:
                setMovementBehaviour(new WanderBehaviour(70f, 0.25f));
                break;
            default:
                setMovementBehaviour(new WanderBehaviour(70f, 0.25f));
        }
    }
}
