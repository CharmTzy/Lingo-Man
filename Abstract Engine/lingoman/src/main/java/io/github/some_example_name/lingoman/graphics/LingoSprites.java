package io.github.some_example_name.lingoman.graphics;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public final class LingoSprites {

    private static final int SPRITE_SIZE = 64;

    private static final Color PLAYER_OUTLINE = new Color(0.07f, 0.16f, 0.22f, 1f);
    private static final Color PLAYER_GOLD = new Color(0.96f, 0.78f, 0.23f, 1f);
    private static final Color PLAYER_GLOW = new Color(0.99f, 0.88f, 0.42f, 1f);
    private static final Color PLAYER_ACCENT = new Color(0.18f, 0.66f, 0.89f, 1f);
    private static final Color PLAYER_EYE = new Color(0.97f, 0.99f, 1f, 1f);
    private static final Color GHOST_EYE = new Color(0.96f, 0.98f, 1f, 1f);
    private static final Color GHOST_PUPIL = new Color(0.08f, 0.16f, 0.24f, 1f);

    private static final Map<Integer, Texture> GHOST_TEXTURES = new HashMap<>();
    private static Texture playerTexture;

    private LingoSprites() {
    }

    public static Texture player() {
        if (playerTexture == null) {
            playerTexture = createPlayerTexture();
        }
        return playerTexture;
    }

    public static Texture ghost(Color color) {
        Color body = color == null ? new Color(1f, 0.3f, 0.3f, 1f) : new Color(color);
        int key = Color.rgba8888(body);
        Texture texture = GHOST_TEXTURES.get(key);
        if (texture != null) {
            return texture;
        }

        Texture created = createGhostTexture(body);
        GHOST_TEXTURES.put(key, created);
        return created;
    }

    public static void disposeAll() {
        if (playerTexture != null) {
            playerTexture.dispose();
            playerTexture = null;
        }

        for (Texture texture : GHOST_TEXTURES.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        GHOST_TEXTURES.clear();
    }

    private static Texture createPlayerTexture() {
        Pixmap pixmap = newTransparentPixmap();
        Color shine = withAlpha(Color.WHITE, 0.28f);

        fillCircle(pixmap, 32, 34, 28, PLAYER_OUTLINE);
        fillTriangle(pixmap, 18, 16, 30, 25, 14, 29, PLAYER_OUTLINE);
        fillCircle(pixmap, 32, 34, 24, PLAYER_GOLD);
        fillTriangle(pixmap, 20, 18, 29, 25, 16, 28, PLAYER_GOLD);
        fillCircle(pixmap, 25, 44, 10, shine);

        fillRect(pixmap, 18, 48, 28, 4, PLAYER_OUTLINE);
        fillRect(pixmap, 20, 49, 24, 2, PLAYER_ACCENT);
        fillCircle(pixmap, 16, 36, 7, PLAYER_OUTLINE);
        fillCircle(pixmap, 48, 36, 7, PLAYER_OUTLINE);
        fillCircle(pixmap, 16, 36, 5, PLAYER_ACCENT);
        fillCircle(pixmap, 48, 36, 5, PLAYER_ACCENT);
        fillCircle(pixmap, 50, 28, 4, PLAYER_OUTLINE);
        fillCircle(pixmap, 50, 28, 2, PLAYER_ACCENT);

        fillRect(pixmap, 26, 45, 12, 4, PLAYER_GLOW);
        fillRect(pixmap, 26, 37, 4, 12, PLAYER_GLOW);

        fillCircle(pixmap, 24, 36, 5, PLAYER_EYE);
        fillCircle(pixmap, 40, 36, 5, PLAYER_EYE);
        fillCircle(pixmap, 26, 35, 2, GHOST_PUPIL);
        fillCircle(pixmap, 42, 35, 2, GHOST_PUPIL);
        fillRect(pixmap, 24, 22, 16, 3, PLAYER_OUTLINE);
        fillRect(pixmap, 27, 19, 10, 3, PLAYER_OUTLINE);
        fillRect(pixmap, 29, 16, 6, 2, PLAYER_ACCENT);

        return toTexture(pixmap);
    }

    private static Texture createGhostTexture(Color bodyColor) {
        Pixmap pixmap = newTransparentPixmap();
        Color outline = shade(bodyColor, 0.32f);
        Color highlight = mix(bodyColor, Color.WHITE, 0.18f);

        fillGhostBody(pixmap, outline, 32, 22, 18, 22);
        fillGhostBody(pixmap, bodyColor, 32, 24, 15, 18);
        fillCircle(pixmap, 25, 44, 8, withAlpha(highlight, 0.45f));

        fillCircle(pixmap, 24, 35, 7, GHOST_EYE);
        fillCircle(pixmap, 40, 35, 7, GHOST_EYE);
        fillCircle(pixmap, 26, 32, 3, GHOST_PUPIL);
        fillCircle(pixmap, 42, 32, 3, GHOST_PUPIL);

        fillRect(pixmap, 25, 23, 14, 2, outline);
        fillRect(pixmap, 27, 21, 3, 2, outline);
        fillRect(pixmap, 34, 21, 3, 2, outline);

        return toTexture(pixmap);
    }

    private static Pixmap newTransparentPixmap() {
        Pixmap pixmap = new Pixmap(SPRITE_SIZE, SPRITE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        return pixmap;
    }

    private static Texture toTexture(Pixmap pixmap) {
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private static void fillTriangle(Pixmap pixmap, int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
        setColor(pixmap, color);
        pixmap.fillTriangle(x1, y1, x2, y2, x3, y3);
    }

    private static void fillGhostBody(Pixmap pixmap, Color color, int centerX, int bottomY, int headRadius, int bodyHeight) {
        setColor(pixmap, color);
        pixmap.fillCircle(centerX, bottomY + bodyHeight, headRadius);
        pixmap.fillRectangle(centerX - headRadius, bottomY, headRadius * 2, bodyHeight);
        pixmap.fillCircle(centerX - headRadius + 6, bottomY, 6);
        pixmap.fillCircle(centerX, bottomY, 6);
        pixmap.fillCircle(centerX + headRadius - 6, bottomY, 6);
    }

    private static void fillCircle(Pixmap pixmap, int x, int y, int radius, Color color) {
        setColor(pixmap, color);
        pixmap.fillCircle(x, y, radius);
    }

    private static void fillRect(Pixmap pixmap, int x, int y, int width, int height, Color color) {
        setColor(pixmap, color);
        pixmap.fillRectangle(x, y, width, height);
    }

    private static void setColor(Pixmap pixmap, Color color) {
        pixmap.setColor(color == null ? Color.WHITE : color);
    }

    private static Color shade(Color color, float amount) {
        return new Color(color.r * (1f - amount), color.g * (1f - amount), color.b * (1f - amount), color.a);
    }

    private static Color mix(Color from, Color to, float amount) {
        float clamped = Math.max(0f, Math.min(1f, amount));
        return new Color(
            from.r + (to.r - from.r) * clamped,
            from.g + (to.g - from.g) * clamped,
            from.b + (to.b - from.b) * clamped,
            from.a + (to.a - from.a) * clamped
        );
    }

    private static Color withAlpha(Color color, float alpha) {
        return new Color(color.r, color.g, color.b, alpha);
    }
}
