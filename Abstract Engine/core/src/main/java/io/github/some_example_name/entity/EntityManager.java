package io.github.some_example_name.entity;

import java.util.ArrayList;
import java.util.List;

public final class EntityManager {

  private final List<Entity> entities = new ArrayList<>();

  public void add(Entity e) { entities.add(e); }

  public void remove(Entity e) { entities.remove(e); }

  public List<Entity> getAll() { return entities; }

  public void clear() { entities.clear(); }
}
