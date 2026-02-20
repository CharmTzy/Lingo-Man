package io.github.some_example_name.demo.entity;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.demo.DemoInputActions;
import io.github.some_example_name.entity.DynamicEntity;
import io.github.some_example_name.managers.InputManager;
import io.github.some_example_name.managers.OutputManager;

public class ControlledBoxEntity extends DynamicEntity {
    private final Color color;
    private final InputManager input;
    private final float moveSpeed;

    public ControlledBoxEntity(InputManager input, float x, float y) {
        super("controlled_box");
        this.color = new Color(0.20f, 0.75f, 1.00f, 1f);
        this.input = input;
        this.moveSpeed = 150f;
        setX(x);
        setY(y);
        setWidth(32);
        setHeight(32);
        setVx(0f);
        setVy(0f);
    }

    @Override
    public void update(float dt) {
        float vx = 0f;
        float vy = 0f;

        if (input.isActionPressed(DemoInputActions.MOVE_LEFT)) {
            vx -= moveSpeed;
        }
        if (input.isActionPressed(DemoInputActions.MOVE_RIGHT)) {
            vx += moveSpeed;
        }
        if (input.isActionPressed(DemoInputActions.MOVE_UP)) {
            vy += moveSpeed;
        }
        if (input.isActionPressed(DemoInputActions.MOVE_DOWN)) {
            vy -= moveSpeed;
        }

        setVx(vx);
        setVy(vy);
    }

    @Override
    public void render(OutputManager outputManager) {
        outputManager.drawRect(getX(), getY(), getWidth(), getHeight(), color);
    }
}
