package io.github.some_example_name.entity;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.managers.InputManager;
import io.github.some_example_name.managers.OutputManager;

public class ControlledBoxEntity extends DynamicEntity {
    private final Color color;
    private final InputManager input;

    public ControlledBoxEntity(InputManager input, float x, float y) {
        super("controlled_box");
        this.color = new Color(0.20f, 0.75f, 1.00f, 1f);
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
        outputManager.drawRect(getX(), getY(), getWidth(), getHeight(), color);
    }
}
