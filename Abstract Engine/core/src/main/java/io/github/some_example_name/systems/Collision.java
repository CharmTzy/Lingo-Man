package io.github.some_example_name.systems;

import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import java.util.List;

public final class Collision {

  public static void update(EntityManager manager) {
    List<Entity> list = manager.getAll();

    for (int i = 0; i < list.size(); i++) {
      Entity a = list.get(i);
      if (!a.isActive())
        continue;

      Rectangle ra = a.bounds();

      for (int j = i + 1; j < list.size(); j++) {
        Entity b = list.get(j);
        if (!b.isActive())
          continue;

        if (ra.overlaps(b.bounds())) {
          a.onCollision(b);
          b.onCollision(a);
        }
      }
    }
  }
}
