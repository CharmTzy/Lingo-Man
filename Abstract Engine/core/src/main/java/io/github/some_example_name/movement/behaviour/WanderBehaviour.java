package io.github.some_example_name.movement.behaviour;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.movement.Movable;

/**
 * Generic wandering behaviour: perturbs velocity slightly over time.
 *
 * <p>Engine-generic: does not reference any specific game rule.
 */
public class WanderBehaviour extends MovementBehaviour {

    private final Random rng = new Random();
    private final float speed;
    private final float turnStrength;

    /**
     * @param speed desired speed magnitude
     * @param turnStrength 0..1-ish; how strongly the direction changes each update
     */
    public WanderBehaviour(float speed, float turnStrength) {
        this.speed = Math.max(0f, speed);
        this.turnStrength = Math.max(0f, turnStrength);
    }

    @Override
    public void move(Movable entity, float deltaTime) {
        Vector2 v = entity.getVelocity();
        if (v == null) v = new Vector2();

        // Random small turn
        float dx = (rng.nextFloat() * 2f - 1f) * turnStrength;
        float dy = (rng.nextFloat() * 2f - 1f) * turnStrength;
        v.add(dx, dy);

        if (v.len2() == 0) {
            v.set(1f, 0f);
        }
        v.nor().scl(speed);
        entity.setVelocity(v);
    }
}
