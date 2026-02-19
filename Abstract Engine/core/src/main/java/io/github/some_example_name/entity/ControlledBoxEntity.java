package io.github.some_example_name.lwjgl3.engine.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.some_example_name.lwjgl3.engine.input.InputManager;

public class ControlledBoxEntity extends Entity {

  private final Texture tex;
  private final float speed;

  public ControlledBoxEntity(Texture tex, float x, float y, float speed) {
    super("controlled_box");
    this.tex = tex;
    this.x = x;
    this.y = y;
    this.speed = speed;
    this.width = 32;
    this.height = 32;
  }

  @Override
  public void update(float dt) {
    vx = 0;
    vy = 0;

    // no diagonal (pac-man style)
    if (InputManager.left()) vx = -speed;
    else if (InputManager.right()) vx = speed;
    else if (InputManager.up()) vy = speed;
    else if (InputManager.down()) vy = -speed;

    // keep inside window (640x480)
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    if (x > 640 - width) x = 640 - width;
    if (y > 480 - height) y = 480 - height;
  }

  @Override
  public void render(Batch batch) {
    batch.draw(tex, x, y, width, height);
  }
}
