package io.github.some_example_name.debug;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.managers.OutputManager;

/**
 * Draws simple debug shapes using OutputManager.
 * Uses a 1x1 pixel texture stretched into lines.
 */
public final class DebugDraw implements Disposable {

    private final Texture pixel;

    public DebugDraw() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1f, 1f, 1f, 1f);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();
    }

    public void rectOutline(OutputManager out, Rectangle r, float thickness, Color color) {
        if (out == null || r == null || color == null) return;
        float t = Math.max(1f, thickness);

        // bottom
        out.drawTinted(pixel, r.x, r.y, r.width, t, color);
        // top
        out.drawTinted(pixel, r.x, r.y + r.height - t, r.width, t, color);
        // left
        out.drawTinted(pixel, r.x, r.y, t, r.height, color);
        // right
        out.drawTinted(pixel, r.x + r.width - t, r.y, t, r.height, color);
    }


    @Override
    public void dispose() {
        pixel.dispose();
    }
}
