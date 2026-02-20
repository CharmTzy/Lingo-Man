package io.github.some_example_name.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * Abstract Engine Input Manager.
 * Handles user input and abstracts specific key bindings from game logic.
 */
public class InputManager {

    // Movement keys
    private int keyUp = Input.Keys.UP;
    private int keyDown = Input.Keys.DOWN;
    private int keyLeft = Input.Keys.LEFT;
    private int keyRight = Input.Keys.RIGHT;
    
    // Action keys
    private int keyAction = Input.Keys.SPACE;
    private int keyPause = Input.Keys.ESCAPE;
    private int keyEnter = Input.Keys.ENTER;
    private int keyRestart = Input.Keys.R;
    private int keyMenu = Input.Keys.M;
    private int keyNpcBehaviourToggle = Input.Keys.TAB;
    private int keyToggleCollisionDebug = Input.Keys.F1;
    private int keySaveSession = Input.Keys.F5;
    private int keyLoadSession = Input.Keys.F9;
    private int keyDeleteSession = Input.Keys.F10;
    private int keyMusicVolumeUp = Input.Keys.EQUALS;
    private int keyMusicVolumeDown = Input.Keys.MINUS;
    private int keyMusicMuteToggle = Input.Keys.V;

    public InputManager() {
        // Constructor can be used to load custom bindings from a file later
    }

    /** Returns true if the UP action key is being held down. */
    public boolean isUp() {
        return Gdx.input.isKeyPressed(keyUp) || Gdx.input.isKeyPressed(Input.Keys.W);
    }

    /** Returns true only on the frame the UP action key is pressed. */
    public boolean isUpJustPressed() {
        return Gdx.input.isKeyJustPressed(keyUp) || Gdx.input.isKeyJustPressed(Input.Keys.W);
    }

    /** Returns true if the DOWN action key is being held down. */
    public boolean isDown() {
        return Gdx.input.isKeyPressed(keyDown) || Gdx.input.isKeyPressed(Input.Keys.S);
    }

    /** Returns true only on the frame the DOWN action key is pressed. */
    public boolean isDownJustPressed() {
        return Gdx.input.isKeyJustPressed(keyDown) || Gdx.input.isKeyJustPressed(Input.Keys.S);
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

    /** Returns true only on the frame the Enter key is pressed. */
    public boolean isEnterJustPressed() {
        return Gdx.input.isKeyJustPressed(keyEnter);
    }

    /** Returns true only on the frame the Restart key is pressed. */
    public boolean isRestartJustPressed() {
        return Gdx.input.isKeyJustPressed(keyRestart);
    }

    /** Returns true only on the frame the Menu key is pressed. */
    public boolean isMenuJustPressed() {
        return Gdx.input.isKeyJustPressed(keyMenu);
    }

    /** Returns true only on the frame the NPC behaviour toggle key is pressed. */
    public boolean isNpcBehaviourToggleJustPressed() {
        return Gdx.input.isKeyJustPressed(keyNpcBehaviourToggle);
    }

    /** Returns true only on the frame the save-session key is pressed. */
    public boolean isSaveSessionJustPressed() {
        return Gdx.input.isKeyJustPressed(keySaveSession);
    }

    /** Returns true only on the frame the load-session key is pressed. */
    public boolean isLoadSessionJustPressed() {
        return Gdx.input.isKeyJustPressed(keyLoadSession);
    }

    /** Returns true only on the frame the delete-session key is pressed. */
    public boolean isDeleteSessionJustPressed() {
        return Gdx.input.isKeyJustPressed(keyDeleteSession);
    }

    /** Returns the current mouse position in screen coordinates. */
    public Vector2 getMousePosition() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }
    
    /** Returns true if the mouse is clicked. */
    public boolean isMouseClicked() {
        return Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    /** Returns true only on the frame the collision debug toggle key is pressed. */
    public boolean isToggleCollisionDebugJustPressed() {
        return Gdx.input.isKeyJustPressed(keyToggleCollisionDebug);
    }

    /** Returns true only on the frame the music-volume-up key is pressed. */
    public boolean isMusicVolumeUpJustPressed() {
        return Gdx.input.isKeyJustPressed(keyMusicVolumeUp) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ADD);
    }

    /** Returns true only on the frame the music-volume-down key is pressed. */
    public boolean isMusicVolumeDownJustPressed() {
        return Gdx.input.isKeyJustPressed(keyMusicVolumeDown) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_SUBTRACT);
    }

    /** Returns true only on the frame the music mute toggle key is pressed. */
    public boolean isMusicMuteToggleJustPressed() {
        return Gdx.input.isKeyJustPressed(keyMusicMuteToggle);
    }

}
