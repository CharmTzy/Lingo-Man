package io.github.some_example_name.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * Abstract Engine Input Manager.
 * Handles user input and abstracts specific key bindings from game logic.
 */
public class InputManager {

    // You can define custom key bindings here
    private int keyUp = Input.Keys.UP;
    private int keyDown = Input.Keys.DOWN;
    private int keyLeft = Input.Keys.LEFT;
    private int keyRight = Input.Keys.RIGHT;
    private int keyAction = Input.Keys.SPACE; // For shooting/interaction
    private int keyPause = Input.Keys.ESCAPE;

    public InputManager() {
        // Constructor can be used to load custom bindings from a file later
    }

    /** Returns true if the UP action key is being held down. */
    public boolean isUp() {
        return Gdx.input.isKeyPressed(keyUp) || Gdx.input.isKeyPressed(Input.Keys.W);
    }

    /** Returns true if the DOWN action key is being held down. */
    public boolean isDown() {
        return Gdx.input.isKeyPressed(keyDown) || Gdx.input.isKeyPressed(Input.Keys.S);
    }

    /** Returns true if the LEFT action key is being held down. */
    public boolean isLeft() {
        return Gdx.input.isKeyPressed(keyLeft) || Gdx.input.isKeyPressed(Input.Keys.A);
    }

    /** Returns true if the RIGHT action key is being held down. */
    public boolean isRight() {
        return Gdx.input.isKeyPressed(keyRight) || Gdx.input.isKeyPressed(Input.Keys.D);
    }

    /** Returns true only on the frame the Action key is pressed (no repeat). */
    public boolean isActionJustPressed() {
        return Gdx.input.isKeyJustPressed(keyAction);
    }
    
    /** Returns true only on the frame the Pause key is pressed. */
    public boolean isPauseJustPressed() {
        return Gdx.input.isKeyJustPressed(keyPause);
    }

    /** Returns the current mouse position in screen coordinates. */
    public Vector2 getMousePosition() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }
    
    /** Returns true if the mouse is clicked. */
    public boolean isMouseClicked() {
        return Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }
}