package io.github.some_example_name.entity;

import com.badlogic.gdx.graphics.Texture;
// 1. Removed: import com.badlogic.gdx.graphics.g2d.Batch;
// 2. Added: Import your custom OutputManager
import io.github.some_example_name.managers.OutputManager;

public class BoxEntity extends Entity {

  private final Texture tex;

  public BoxEntity(Texture tex, float x, float y) {
    super("box");
    this.tex = tex;
    this.x = x;
    this.y = y;
    this.width = 32;
    this.height = 32;
    this.vx = 120f; // move right
    this.vy = 0f;
  }

  // 3. Changed: Replaced Batch with OutputManager
  @Override
  public void render(OutputManager outputManager) {
    // 4. Changed: Use the OutputManager to draw instead of Batch
    outputManager.draw(tex, x, y, width, height);
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