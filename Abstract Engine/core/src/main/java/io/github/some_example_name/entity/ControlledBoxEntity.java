package io.github.some_example_name.entity; // FIXED PACKAGE

import com.badlogic.gdx.graphics.Texture;
import io.github.some_example_name.managers.InputManager;
import io.github.some_example_name.managers.OutputManager;

public class ControlledBoxEntity extends Entity {
    private final Texture tex;
    private final InputManager input;

    public ControlledBoxEntity(Texture tex, InputManager input, float x, float y) {
        super("controlled_box");
        this.tex = tex;
        this.input = input;
        setX(x);
        setY(y);
        setWidth(32);
        setHeight(32);
        setVx(150f); // speed
        setVy(150f);
    }

    @Override
    public void update(float dt) {
        // Use your abstracted InputManager
        if (input.isLeft()) setX(getX() - getVx() * dt);
        if (input.isRight()) setX(getX() + getVx() * dt);
        if (input.isUp()) setY(getY() + getVy() * dt);
        if (input.isDown()) setY(getY() - getVy() * dt);
    }

    @Override
    public void render(OutputManager outputManager) {
        outputManager.draw(tex, getX(), getY(), getWidth(), getHeight());
    }
}