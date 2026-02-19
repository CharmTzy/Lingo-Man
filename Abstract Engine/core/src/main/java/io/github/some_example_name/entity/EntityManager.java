package io.github.some_example_name.entity;

import java.util.ArrayList;
import java.util.List;
import io.github.some_example_name.managers.OutputManager;
import java.util.Collections;

public final class EntityManager {

  private final List<Entity> entities = new ArrayList<>();

  public void add(Entity e) { entities.add(e); }

  public void remove(Entity e) { entities.remove(e); }

public List<Entity> getAll() {
    return Collections.unmodifiableList(entities);
}

  public void clear() { entities.clear(); }

  // Added loop to update all active entities
  public void update(float dt) {
      for (int i = 0; i < entities.size(); i++) {
          Entity e = entities.get(i);
          if (e.isActive()) {
              e.update(dt);
          }
      }
  }

  // Added loop to render all active entities using the OutputManager
  public void render(OutputManager out) {
      for (int i = 0; i < entities.size(); i++) {
          Entity e = entities.get(i);
          if (e.isActive()) {
              e.render(out);
          }
      }
  }
}