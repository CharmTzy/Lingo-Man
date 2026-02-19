package io.github.some_example_name.entity; // FIXED PACKAGE

import com.badlogic.gdx.graphics.Texture;
import io.github.some_example_name.managers.OutputManager;

public class ChasingBoxEntity extends Entity {
    private final Texture tex;
    private final Entity target;

    public ChasingBoxEntity(Texture tex, Entity target, float x, float y) {
        super("chasing_box");
        this.tex = tex;
        this.target = target;
        setX(x);
        setY(y);
        setWidth(32);
        setHeight(32);
        setVx(80f); // chasing speed
        setVy(80f);
    }

    @Override
    public void update(float dt) {
        if (target == null) return;

        // Simple chase logic using your encapsulated getters/setters
        if (getX() < target.getX()) setX(getX() + getVx() * dt);
        if (getX() > target.getX()) setX(getX() - getVx() * dt);
        if (getY() < target.getY()) setY(getY() + getVy() * dt);
        if (getY() > target.getY()) setY(getY() - getVy() * dt);
    }

    @Override
    public void render(OutputManager outputManager) {
        outputManager.draw(tex, getX(), getY(), getWidth(), getHeight());
    }
}