package io.github.some_example_name.lwjgl3.engine.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class BoxEntity extends Entity {

  private final Texture tex;

  public BoxEntity(Texture tex, float x, float y) {
    this.tex = tex;
    this.x = x;
    this.y = y;
    this.width = 32;
    this.height = 32;
    this.vx = 120f; // move right
    this.vy = 0f;
  }

  @Override
  public void render(Batch batch) {
    batch.draw(tex, x, y, width, height);
  }

  @Override
  public void update(float dt) {
    // bounce in 640 width window
    if (x < 0)
      vx = Math.abs(vx);
    if (x > 640 - width)
      vx = -Math.abs(vx);
  }
}
