package io.github.some_example_name.lingoman.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.movement.Movable;
import io.github.some_example_name.movement.behaviour.MovementBehaviour;

public final class RecoveringMazeBehaviour extends MovementBehaviour {

    private final MovementBehaviour primary;
    private final MovementBehaviour fallback;
    private final float stallSeconds;
    private final float recoverySeconds;
    private final float minTravelDistance;
    private final float minRequestedSpeed;

    private final Vector2 lastPosition = new Vector2();
    private boolean initialized = false;
    private float stalledFor = 0f;
    private float recoveringFor = 0f;

    public RecoveringMazeBehaviour(
        MovementBehaviour primary,
        MovementBehaviour fallback,
        float stallSeconds,
        float recoverySeconds,
        float minTravelDistance
    ) {
        this.primary = primary;
        this.fallback = fallback;
        this.stallSeconds = Math.max(0.05f, stallSeconds);
        this.recoverySeconds = Math.max(0.05f, recoverySeconds);
        this.minTravelDistance = Math.max(0.5f, minTravelDistance);
        this.minRequestedSpeed = this.minTravelDistance / Math.max(this.stallSeconds, 0.05f);
    }

    @Override
    public void move(Movable entity, float deltaTime) {
        Vector2 currentPosition = entity.getPosition();
        if (!initialized) {
            lastPosition.set(currentPosition);
            initialized = true;
        }

        if (recoveringFor > 0f) {
            fallback.move(entity, deltaTime);
            recoveringFor = Math.max(0f, recoveringFor - deltaTime);
        } else {
            primary.move(entity, deltaTime);
        }

        float distanceMoved = currentPosition.dst(lastPosition);
        float requestedSpeed = entity.getVelocity().len();
        boolean tryingToMove = requestedSpeed >= minRequestedSpeed;

        if (tryingToMove && distanceMoved < minTravelDistance) {
            stalledFor += deltaTime;
            if (stalledFor >= stallSeconds) {
                stalledFor = 0f;
                recoveringFor = recoverySeconds;
            }
        } else {
            stalledFor = 0f;
        }

        lastPosition.set(currentPosition);
    }
}
