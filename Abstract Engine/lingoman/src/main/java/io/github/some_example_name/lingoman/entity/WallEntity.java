package io.github.some_example_name.lingoman.entity;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.managers.OutputManager;

public class WallEntity extends Entity {

    private final Color color = new Color(0.15f, 0.25f, 0.35f, 1f);

    public WallEntity(String id, float x, float y, float size) {
        super(id);
        setX(x);
        setY(y);
        setWidth(size);
        setHeight(size);
    }

    @Override
    public boolean isStatic() { return true; }

    @Override
    public void render(OutputManager outputManager) {
        outputManager.drawRect(getX(), getY(), getWidth(), getHeight(), color);
    }
}