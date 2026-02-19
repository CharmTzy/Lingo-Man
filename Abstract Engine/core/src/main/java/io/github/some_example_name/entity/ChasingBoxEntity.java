package io.github.some_example_name.lwjgl3.engine.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class ChasingBoxEntity extends Entity {

  private final Texture tex;
  private final Entity target;
  private final float speed;

  // choose direction every few frames to avoid jitter
  private float decisionTimer = 0f;
  private final float decisionInterval = 0.15f;

  public ChasingBoxEntity(Texture tex, float x, float y, float speed,
                          Entity target) {
    super("chasing_box");
    this.tex = tex;
    this.x = x;
    this.y = y;
    this.speed = speed;
    this.target = target;
    this.width = 32;
    this.height = 32;
  }

  @Override
  public void update(float dt) {
    decisionTimer -= dt;

    if (decisionTimer <= 0f) {
      decisionTimer = decisionInterval;

      float dx = target.x - x;
      float dy = target.y - y;

      // move on one axis only (pac-man style)
      if (Math.abs(dx) > Math.abs(dy)) {
        vx = (dx >= 0) ? speed : -speed;
        vy = 0;
      } else {
        vy = (dy >= 0) ? speed : -speed;
        vx = 0;
      }
    }

    // keep inside window
    if (x < 0) {
      x = 0;
      vx = Math.abs(vx);
    }
    if (y < 0) {
      y = 0;
      vy = Math.abs(vy);
    }
    if (x > 640 - width) {
      x = 640 - width;
      vx = -Math.abs(vx);
    }
    if (y > 480 - height) {
      y = 480 - height;
      vy = -Math.abs(vy);
    }
  }

  @Override
  public void render(Batch batch) {
    batch.draw(tex, x, y, width, height);
  }
}
