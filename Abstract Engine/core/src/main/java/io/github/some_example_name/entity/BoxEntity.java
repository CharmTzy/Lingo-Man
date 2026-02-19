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
    setX(x);
    setY(y);
    setWidth(32);
    setHeight(32);
    setVx(120f); // move right
    setVy(0f);
  }

  // 3. Changed: Replaced Batch with OutputManager
  @Override
  public void render(OutputManager outputManager) {
    // 4. Changed: Use the OutputManager to draw instead of Batch
    outputManager.draw(tex, getX(), getY(), getWidth(), getHeight());
  }

  @Override
  public void update(float dt) {
    // bounce in 640 width window
    if (getX() < 0)
      setVx(Math.abs(getVx()));
    if (getX() > 640 - getWidth())
      setVx(-Math.abs(getVx()));
  }
}