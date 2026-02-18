package io.github.some_example_name.entities;

/**
 * A concrete, engine-generic NPC entity.
 *
 * <p>This class is intentionally minimal: concrete games can extend it further
 * (e.g., add sprites, state machines, etc.).
 */
public class NPCEntity extends DynamicEntity {

    public NPCEntity(String id) {
        super(id);
    }
}
