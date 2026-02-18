package io.github.some_example_name.lwjgl3.engine.scene;

import com.badlogic.gdx.graphics.Texture;
import io.github.some_example_name.lwjgl3.engine.entity.BoxEntity;

public class TestScene extends SceneBase {

  private Texture box;

  @Override
  public void onEnter() {
    box = new Texture("box.png");
    entities.add(new BoxEntity(box, 50, 200));
  }

  @Override
  public void onExit() {
    box.dispose();
    super.onExit();
  }
}
