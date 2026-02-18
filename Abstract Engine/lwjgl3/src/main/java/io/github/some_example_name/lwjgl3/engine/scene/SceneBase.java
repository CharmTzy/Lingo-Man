package io.github.some_example_name.lwjgl3.engine.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.some_example_name.lwjgl3.engine.entity.Entity;
import io.github.some_example_name.lwjgl3.engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.engine.systems.Collision;
import io.github.some_example_name.lwjgl3.engine.systems.Movement;

/**
 * Part 1 engine:
 * - owns entities
 * - runs update + movement + collision
 * - renders entities
 * Game-specific scenes (PacmanScene later) extend this.
 */
public abstract class SceneBase implements IScene {

  protected final EntityManager entities = new EntityManager();

  @Override
  public void onEnter() {
    // optional: scenes can override and spawn entities here
  }

  @Override
  public void onExit() {
    entities.clear();
  }

  @Override
  public void update(float dt) {
    // 1) entity custom logic
    for (Entity e : entities.getAll()) {
      if (e.active)
        e.update(dt);
    }

    // 2) engine systems (keep these engine-level)
    Movement.update(entities, dt);
    Collision.update(entities);
  }

  @Override
  public void render(Batch batch) {
    for (Entity e : entities.getAll()) {
      if (e.active)
        e.render(batch);
    }
  }
}
