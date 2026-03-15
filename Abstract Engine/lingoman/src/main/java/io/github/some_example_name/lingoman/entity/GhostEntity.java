package io.github.some_example_name.lingoman.entity;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.NPCEntity;
import io.github.some_example_name.managers.OutputManager;

public class GhostEntity extends NPCEntity {

    private final Color color;

    public GhostEntity(String id, Color color, float x, float y, float size) {
        super(id);
        this.color = color == null ? new Color(1f, 0.3f, 0.3f, 1f) : color;
        setX(x);
        setY(y);
        setWidth(size);
        setHeight(size);
    }

    @Override
    public void update(float dt) {
        if (dt <= 0f || getMovementBehaviour() == null) {
            return;
        }

        getMovementBehaviour().move(this, dt);
        setX(getX() + getVx() * dt);
        setY(getY() + getVy() * dt);
    }

    @Override
    public void render(OutputManager outputManager) {
        outputManager.drawRect(getX(), getY(), getWidth(), getHeight(), color);
    }

    @Override
    public void onCollision(Entity other) {
        if (other instanceof WallEntity) {
            setVx(0f);
            setVy(0f);
        }
    }
}
