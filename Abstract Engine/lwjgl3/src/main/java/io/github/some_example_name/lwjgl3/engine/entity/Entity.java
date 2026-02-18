package io.github.some_example_name.lwjgl3.engine.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Entity {

  public float x, y;
  public float vx, vy;
  public float width = 32, height = 32;

  public boolean active = true;

  public void update(float dt) {}
  public void render(Batch batch) {}

  public void onCollision(Entity other) {}

  public Rectangle bounds() { return new Rectangle(x, y, width, height); }
}
