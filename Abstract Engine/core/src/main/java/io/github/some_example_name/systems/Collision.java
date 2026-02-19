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
          resolveOverlap(a, b);
          a.onCollision(b);
          b.onCollision(a);
        }
      }
    }
  }

  private static void resolveOverlap(Entity a, Entity b) {
    Rectangle ra = a.bounds();
    Rectangle rb = b.bounds();

    float overlapX = Math.min(ra.x + ra.width, rb.x + rb.width) - Math.max(ra.x, rb.x);
    float overlapY = Math.min(ra.y + ra.height, rb.y + rb.height) - Math.max(ra.y, rb.y);

    if (overlapX <= 0f || overlapY <= 0f) {
      return;
    }

    if (overlapX < overlapY) {
      float separation = overlapX * 0.5f;
      if (ra.x < rb.x) {
        a.setX(a.getX() - separation);
        b.setX(b.getX() + separation);
      } else {
        a.setX(a.getX() + separation);
        b.setX(b.getX() - separation);
      }
      return;
    }

    float separation = overlapY * 0.5f;
    if (ra.y < rb.y) {
      a.setY(a.getY() - separation);
      b.setY(b.getY() + separation);
    } else {
      a.setY(a.getY() + separation);
      b.setY(b.getY() - separation);
    }
  }
}
