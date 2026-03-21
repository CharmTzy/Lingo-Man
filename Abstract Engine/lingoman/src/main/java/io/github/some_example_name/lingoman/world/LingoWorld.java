package io.github.some_example_name.lingoman.world;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.collision.ICollisionListener;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.lingoman.LingoCollisionFilter;
import io.github.some_example_name.lingoman.entity.GhostEntity;
import io.github.some_example_name.lingoman.movement.FrozenAwareBehaviour;
import io.github.some_example_name.managers.CollisionManager;
import io.github.some_example_name.managers.MovementManager;
import io.github.some_example_name.managers.OutputManager;
import io.github.some_example_name.movement.Movable;
import io.github.some_example_name.movement.behaviour.MovementBehaviour;

/**
 * Coordinates gameplay entities and keeps add/remove logic in one place.
 *
 * <p>The scene can focus on rules while this class manages engine registration
 * for rendering, movement, collisions, and deferred cleanup.
 *
 * <h3>Movement registration strategy</h3>
 * <ul>
 *   <li><strong>Non-ghost Movables</strong> (player, fireballs) — registered with
 *       physics <em>enabled</em>. {@link MovementPhysics} integrates their
 *       position after the behaviour runs each frame.</li>
 *   <li><strong>GhostEntity</strong> — registered with physics <em>disabled</em>.
 *       The manager still dispatches their {@link FrozenAwareBehaviour} every
 *       frame; the ghost self-integrates its position inside its own
 *       {@code update()} to preserve precise maze-cell alignment.</li>
 * </ul>
 */
public final class LingoWorld {

    private final EntityManager entityManager = new EntityManager();
    private final CollisionManager collisionManager = new CollisionManager();
    private final MovementManager movementManager = new MovementManager();
    private final Set<Entity> entities = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<Entity, Collider> collidersByEntity = new IdentityHashMap<>();
    private final Set<Movable> movables = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
    private final List<Entity> pendingRemoval = new ArrayList<>();

    public LingoWorld() {
        collisionManager.setFilter(new LingoCollisionFilter());
    }

    // ── Entity lifecycle ──────────────────────────────────────────────────────

    /**
     * Add an entity to the world.
     *
     * <p>If the entity implements {@link Movable}:
     * <ul>
     *   <li>Ghosts are registered with physics <strong>disabled</strong> — they
     *       self-integrate position so that maze navigation remains frame-accurate.</li>
     *   <li>All other Movables are registered with physics <strong>enabled</strong>
     *       so {@code MovementPhysics} handles position integration.</li>
     * </ul>
     */
    public void addEntity(Entity entity) {
        if (entity == null) {
            return;
        }
        entity.setActive(true);
        if (entities.add(entity)) {
            entityManager.add(entity);
        }

        if (entity instanceof Movable movable && movables.add(movable)) {
            if (entity instanceof GhostEntity) {
                // Physics disabled: manager dispatches behaviour, ghost self-integrates
                movementManager.registerEntity(movable, false);
            } else {
                // Physics enabled: manager dispatches behaviour + integrates position
                movementManager.registerEntity(movable, true);
            }
        }
    }

    public Collider addCollidableEntity(Entity entity, ICollisionListener listener) {
        if (entity == null) {
            return null;
        }

        addEntity(entity);
        Collider existing = collidersByEntity.get(entity);
        if (existing != null) {
            existing.setListener(listener);
            return existing;
        }

        Collider collider = new Collider(entity, entity.getWidth(), entity.getHeight());
        collider.setListener(listener);
        collidersByEntity.put(entity, collider);
        collisionManager.add(collider);
        return collider;
    }

    /**
     * Assign a movement behaviour to a movable entity.
     *
     * <p>For {@link GhostEntity}, the supplied behaviour is wrapped in a
     * {@link FrozenAwareBehaviour} before being handed to the
     * {@link MovementManager}. This prevents the manager from calling
     * {@code move()} while the ghost is frozen, without requiring any changes to
     * engine code (OCP).
     *
     * <p>For all other entities the behaviour is assigned directly via the manager.
     */
    public void assignBehaviour(Movable movable, MovementBehaviour behaviour) {
        if (movable == null) {
            return;
        }

        if (movable instanceof GhostEntity ghost) {
            // Ensure ghost is registered (may be called before addEntity in some flows)
            if (movables.add(ghost)) {
                movementManager.registerEntity(ghost, false);
            }
            // Wrap with FrozenAwareBehaviour so the manager skips move() when frozen
            MovementBehaviour wrapped = new FrozenAwareBehaviour(ghost, behaviour);
            movementManager.assignBehaviour(ghost, wrapped);
            return;
        }

        // Non-ghost: register if not already done, then assign directly
        if (movables.add(movable)) {
            movementManager.registerEntity(movable, true);
        }
        movementManager.assignBehaviour(movable, behaviour);
    }

    public void removeEntity(Entity entity) {
        if (entity == null || pendingRemoval.contains(entity)) {
            return;
        }
        entity.setActive(false);
        pendingRemoval.add(entity);
    }

    public Collider getCollider(Entity entity) {
        return collidersByEntity.get(entity);
    }

    // ── Per-frame update ──────────────────────────────────────────────────────

    public void update(float deltaTime) {
        entityManager.update(deltaTime);
        movementManager.updateAll(deltaTime);
        collisionManager.update();
        flushPendingRemoval();
    }

    public void render(OutputManager outputManager) {
        entityManager.render(outputManager);
    }

    // ── Cleanup ───────────────────────────────────────────────────────────────

    public void clear() {
        pendingRemoval.clear();

        for (Collider collider : new ArrayList<>(collidersByEntity.values())) {
            collisionManager.remove(collider);
        }
        entities.clear();
        collidersByEntity.clear();

        for (Movable movable : new ArrayList<>(movables)) {
            movementManager.unregisterEntity(movable);
        }
        movables.clear();

        entityManager.clear();
    }

    private void flushPendingRemoval() {
        if (pendingRemoval.isEmpty()) {
            return;
        }

        for (Entity entity : new ArrayList<>(pendingRemoval)) {
            Collider collider = collidersByEntity.remove(entity);
            if (collider != null) {
                collisionManager.remove(collider);
            }

            if (entity instanceof Movable movable && movables.remove(movable)) {
                movementManager.unregisterEntity(movable);
            }

            entities.remove(entity);
            entityManager.remove(entity);
        }

        pendingRemoval.clear();
    }
}
