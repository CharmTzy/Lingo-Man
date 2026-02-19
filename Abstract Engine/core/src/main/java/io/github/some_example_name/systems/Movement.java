package io.github.some_example_name.systems;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;

public final class Movement {

  public static void update(EntityManager manager, float dt) {
    for (Entity e : manager.getAll()) {
      if (!e.isActive())
        continue;

      e.setX(e.getX() + e.getVx() * dt);
      e.setY(e.getY() + e.getVy() * dt);
    }
  }
}
