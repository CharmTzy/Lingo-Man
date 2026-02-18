package io.github.some_example_name.lwjgl3.engine.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import java.util.*;

public final class EntityManager {

  private final Map<Long, IEntity> byId = new HashMap<>();
  private final ArrayList<IEntity> entities = new ArrayList<>();

  private final ArrayList<IEntity> toAdd = new ArrayList<>();
  private final ArrayList<IEntity> toRemove = new ArrayList<>();

  private boolean locked = false;

  public void add(IEntity entity) {
    if (entity == null)
      throw new IllegalArgumentException("entity is null");
    if (locked)
      toAdd.add(entity);
    else
      addNow(entity);
  }

  public void remove(IEntity entity) {
    if (entity == null)
      return;
    if (locked)
      toRemove.add(entity);
    else
      removeNow(entity);
  }

  public Optional<IEntity> getById(long id) {
    return Optional.ofNullable(byId.get(id));
  }

  public List<IEntity> getAllSnapshot() {
    return Collections.unmodifiableList(new ArrayList<>(entities));
  }

  public void clear() {
    if (locked) {
      toRemove.addAll(entities);
      return;
    }
    for (IEntity e : entities)
      e.onRemoved();
    entities.clear();
    byId.clear();
    toAdd.clear();
    toRemove.clear();
  }

  public void update(float dt) {
    locked = true;

    for (int i = 0; i < entities.size(); i++) {
      IEntity e = entities.get(i);
      if (!e.isActive())
        continue;
      if (e instanceof IUpdatable u)
        u.update(dt);
    }

    locked = false;
    flushQueues();
  }

  public void render(Batch batch) {
    locked = true;

    ArrayList<IEntity> renderables = new ArrayList<>();
    for (IEntity e : entities) {
      if (!e.isActive())
        continue;
      if (e instanceof IRenderable)
        renderables.add(e);
    }

    renderables.sort((a, b) -> Integer.compare(layerOf(a), layerOf(b)));

    for (IEntity e : renderables) {
      ((IRenderable)e).render(batch);
    }

    locked = false;
    flushQueues();
  }

  private int layerOf(IEntity e) {
    if (e instanceof ILayered l)
      return l.getLayer();
    return 0;
  }

  private void addNow(IEntity entity) {
    long id = entity.getId();
    if (byId.containsKey(id))
      throw new IllegalStateException("Duplicate entity id: " + id);
    byId.put(id, entity);
    entities.add(entity);
    entity.onAdded();
  }

  private void removeNow(IEntity entity) {
    long id = entity.getId();
    if (!byId.containsKey(id))
      return;

    byId.remove(id);
    entities.remove(entity);
    entity.onRemoved();
  }

  private void flushQueues() {
    if (!toRemove.isEmpty()) {
      for (IEntity e : toRemove)
        removeNow(e);
      toRemove.clear();
    }
    if (!toAdd.isEmpty()) {
      for (IEntity e : toAdd)
        addNow(e);
      toAdd.clear();
    }
  }
}
