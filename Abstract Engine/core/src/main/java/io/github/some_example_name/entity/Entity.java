package io.github.some_example_name.entity;

import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.managers.OutputManager;

public abstract class Entity {
  private final String id;
  
  protected Entity(String id) {
      this.id = id;
  }
  
  public String getId() {
      return id;
  }
  
  public float x, y;
  public float vx, vy;
  public float width = 32, height = 32;

  public boolean active = true;

  public void update(float dt) {}
  
  // Replaced Batch with OutputManager
  public void render(OutputManager outputManager) {}

  public void onCollision(Entity other) {}

  public Rectangle bounds() { return new Rectangle(x, y, width, height); }
}