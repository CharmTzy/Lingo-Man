package io.github.some_example_name.lwjgl3.engine.entity;

import com.badlogic.gdx.graphics.g2d.Batch;

public final class World {
  private final EntityManager entityManager = new EntityManager();

  public EntityManager entities() { return entityManager; }

  public void update(float dt) { entityManager.update(dt); }

  public void render(Batch batch) { entityManager.render(batch); }

  public void clear() { entityManager.clear(); }
}
