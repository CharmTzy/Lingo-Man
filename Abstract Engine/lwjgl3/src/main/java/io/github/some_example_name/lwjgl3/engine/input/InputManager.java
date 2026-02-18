package io.github.some_example_name.lwjgl3.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public final class InputManager {

  public static boolean left() {
    return Gdx.input.isKeyPressed(Input.Keys.LEFT);
  }
  public static boolean right() {
    return Gdx.input.isKeyPressed(Input.Keys.RIGHT);
  }
  public static boolean up() { return Gdx.input.isKeyPressed(Input.Keys.UP); }
  public static boolean down() {
    return Gdx.input.isKeyPressed(Input.Keys.DOWN);
  }
}
