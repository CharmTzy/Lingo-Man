package io.github.some_example_name.debug;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.managers.CollisionManager;
import io.github.some_example_name.managers.OutputManager;
import com.badlogic.gdx.graphics.Color;

public final class CollisionDebugOverlay {

    private final DebugDraw draw;
    private float normalThickness = 2f;
    private float collidingThickness = 4f;

    private Color normalColor = Color.GREEN;
    private Color collidingColor = Color.RED;

    public CollisionDebugOverlay(DebugDraw draw) {
        this.draw = draw;
    }

    public void render(OutputManager out, CollisionManager cm) {
        if (out == null || cm == null) return;

        for (Collider c : cm.getAllColliders()) {
            if (c == null) continue;

            boolean hit = cm.isColliding(c);
            float thickness = hit ? collidingThickness : normalThickness;
            Color color = hit ? collidingColor : normalColor;

            draw.rectOutline(out, c.getBounds(), thickness, color);
        }
    }
}
