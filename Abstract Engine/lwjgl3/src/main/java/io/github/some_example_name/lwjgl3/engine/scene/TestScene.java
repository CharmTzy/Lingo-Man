package io.github.some_example_name.lwjgl3.engine.scene;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import io.github.some_example_name.lwjgl3.engine.entity.ChasingBoxEntity;
import io.github.some_example_name.lwjgl3.engine.entity.ControlledBoxEntity;

public class TestScene extends SceneBase {

  private Texture texControlled;
  private Texture texChaser;

  private BitmapFont font;

  private ControlledBoxEntity controlled;
  private ChasingBoxEntity chaser;

  @Override
  public void onEnter() {
    texControlled = new Texture("box.png");
    texChaser = new Texture("monster.png");

    controlled = new ControlledBoxEntity(texControlled, 100, 200, 150f);
    chaser = new ChasingBoxEntity(texChaser, 450, 200, 120f, controlled);

    entities.add(controlled);
    entities.add(chaser);

    font = new BitmapFont();
  }

  @Override
  public void render(com.badlogic.gdx.graphics.g2d.Batch batch) {
    super.render(batch);

    float hudX = 10f;
    float hudY = 480f - 10f;

    String line1 = "Controls: Arrow keys";
    String line2 = "You = controlled shape (Pac-Man role)";
    String line3 = "Follower = chasing shape (Ghost role)";
    String line4 =
        String.format("You: (%.0f, %.0f)   Follower: (%.0f, %.0f)",
                      controlled.x, controlled.y, chaser.x, chaser.y);

    font.draw(batch, line1, hudX, hudY);
    font.draw(batch, line2, hudX, hudY - 18f);
    font.draw(batch, line3, hudX, hudY - 36f);
    font.draw(batch, line4, hudX, hudY - 54f);
  }

  @Override
  public void onExit() {
    texControlled.dispose();
    texChaser.dispose();
    font.dispose();
    super.onExit();
  }
}
