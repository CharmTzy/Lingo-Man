package io.github.some_example_name.lwjgl3.engine.systems;

import io.github.some_example_name.lwjgl3.engine.entity.Entity;
import io.github.some_example_name.lwjgl3.engine.entity.EntityManager;

public final class Movement {

  public static void update(EntityManager manager, float dt) {
    for (Entity e : manager.getAll()) {
      if (!e.active)
        continue;

      e.x += e.vx * dt;
      e.y += e.vy * dt;
    }
  }
}
