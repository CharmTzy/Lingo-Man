package io.github.some_example_name.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.some_example_name.lifecycle.Activatable;
import io.github.some_example_name.movement.Movable;
import io.github.some_example_name.movement.behaviour.MovementBehaviour;
import io.github.some_example_name.movement.physics.MovementPhysics;

/**
 * Coordinates movement for all registered {@link Movable} entities.
 *
 * <p>Important design rule (Abstract Engine): this manager only coordinates.
 * The "dirty work" lives in {@link MovementBehaviour} (movement decision) and
 * {@link MovementPhysics} (integration/calculation).
 *
 * <p>Entities that require precise self-managed position integration (e.g. grid-
 * following AI) can be registered with physics disabled via
 * {@link #registerEntity(Movable, boolean)}. The manager still dispatches their
 * behaviour each frame; they are responsible for integrating their own position.
 */
public class MovementManager {

    private final List<Movable> movableEntities = new ArrayList<>();
    private final Map<Movable, MovementBehaviour> behaviours = new IdentityHashMap<>();
    private final MovementPhysics physics = new MovementPhysics();

    /**
     * Tracks entities for which {@link MovementPhysics#integrate} should be
     * skipped. The behaviour is still invoked; position integration is left to
     * the entity itself.
     */
    private final Set<Movable> physicsDisabled =
            Collections.newSetFromMap(new IdentityHashMap<>());

    // ── Registration ──────────────────────────────────────────────────────────

    /**
     * Register an entity with full physics enabled (behaviour + integration).
     * Equivalent to {@code registerEntity(entity, true)}.
     */
    public void registerEntity(Movable entity) {
        registerEntity(entity, true);
    }

    /**
     * Register an entity so it is included in {@link #updateAll(float)}.
     *
     * @param entity        the movable entity to register
     * @param enablePhysics if {@code true} {@link MovementPhysics#integrate} is
     *                      called after the behaviour each frame; if {@code false}
     *                      only the behaviour is invoked and the entity must
     *                      integrate its own position
     */
    public void registerEntity(Movable entity, boolean enablePhysics) {
        if (entity == null) return;
        if (!movableEntities.contains(entity)) {
            movableEntities.add(entity);
        }
        if (!enablePhysics) {
            physicsDisabled.add(entity);
        } else {
            // Ensure a previously-disabled entity is re-enabled if re-registered
            physicsDisabled.remove(entity);
        }
    }

    /**
     * Unregister an entity so it is no longer updated.
     * Also cleans up any physics-disabled flag for that entity.
     */
    public void unregisterEntity(Movable entity) {
        if (entity == null) return;
        movableEntities.remove(entity);
        behaviours.remove(entity);
        physicsDisabled.remove(entity);
    }

    /** Assign a movement behaviour to an entity (Strategy Pattern). */
    public void assignBehaviour(Movable entity, MovementBehaviour behaviour) {
        if (entity == null) return;
        if (behaviour == null) {
            behaviours.remove(entity);
            entity.setMovementBehaviour(null);
            return;
        }
        behaviours.put(entity, behaviour);
        entity.setMovementBehaviour(behaviour);
    }

    // ── Per-frame update ──────────────────────────────────────────────────────

    /**
     * Update all registered entities.
     *
     * <ol>
     *   <li>Ask the entity's behaviour to update velocity/direction/state.</li>
     *   <li>Delegate position integration to {@link MovementPhysics} — unless
     *       physics is disabled for this entity, in which case the entity
     *       handles its own integration.</li>
     * </ol>
     */
    public void updateAll(float deltaTime) {
        if (deltaTime <= 0) return;

        for (Movable entity : new ArrayList<>(movableEntities)) {
            if (entity == null) continue;
            if (!isActive(entity)) continue;

            MovementBehaviour behaviour = behaviours.get(entity);
            if (behaviour == null) {
                behaviour = entity.getMovementBehaviour();
            }

            if (behaviour != null) {
                behaviour.move(entity, deltaTime);
            }

            // Only integrate position for entities that have NOT opted out of physics
            if (!physicsDisabled.contains(entity)) {
                physics.integrate(entity, deltaTime);
            }
        }
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    /** Exposes the physics helper (useful for configuration/testing). */
    public MovementPhysics getPhysics() {
        return physics;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean isActive(Object candidate) {
        return !(candidate instanceof Activatable activatable) || activatable.isActive();
    }
}
