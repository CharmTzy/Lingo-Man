package io.github.some_example_name.lingoman.entity;

import io.github.some_example_name.lingoman.LingoInputActions;
import io.github.some_example_name.entity.DynamicEntity;
import io.github.some_example_name.lingoman.graphics.LingoSprites;
import io.github.some_example_name.managers.InputManager;
import io.github.some_example_name.managers.OutputManager;

public class PlayerEntity extends DynamicEntity {
    private final InputManager input;
    private final float moveSpeed;

    public PlayerEntity(String id, InputManager input, float x, float y, float moveSpeed, float size) {
        super(id);
        this.input = input;
        this.moveSpeed = moveSpeed;
        setX(x);
        setY(y);
        setWidth(size);
        setHeight(size);
    }

    @Override
    public void update(float dt) {
        float vx = 0f;
        float vy = 0f;

        if (input != null) {
            if (input.isActionPressed(LingoInputActions.MOVE_LEFT)) {
                vx -= moveSpeed;
            }
            if (input.isActionPressed(LingoInputActions.MOVE_RIGHT)) {
                vx += moveSpeed;
            }
            if (input.isActionPressed(LingoInputActions.MOVE_UP)) {
                vy += moveSpeed;
            }
            if (input.isActionPressed(LingoInputActions.MOVE_DOWN)) {
                vy -= moveSpeed;
            }
        }

        setVx(vx);
        setVy(vy);
    }

    @Override
    public void render(OutputManager outputManager) {
        outputManager.draw(LingoSprites.player(), getX(), getY(), getWidth(), getHeight());
    }
}
