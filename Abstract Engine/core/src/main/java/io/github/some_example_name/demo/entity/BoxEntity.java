package io.github.some_example_name.demo.entity;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.managers.OutputManager;

public class BoxEntity extends Entity {

  private final Color color;

  public BoxEntity(float x, float y) {
    super("box");
    this.color = new Color(0.95f, 0.90f, 0.20f, 1f);
    setX(x);
    setY(y);
    setWidth(32);
    setHeight(32);
    setVx(120f); // move right
    setVy(0f);
  }

  @Override
  public void render(OutputManager outputManager) {
    outputManager.drawRect(getX(), getY(), getWidth(), getHeight(), color);
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
