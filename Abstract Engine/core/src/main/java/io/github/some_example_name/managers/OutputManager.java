package io.github.some_example_name.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * Abstract Engine Output Manager.
 * Handles rendering graphics and text to the screen.
 */
public class OutputManager implements Disposable {

    private SpriteBatch batch;
    private BitmapFont font;
    
    // Abstract Engine requirement: Encapsulate the "How" (LibGDX Batch) 
    // from the "What" (Draw this texture).

    public OutputManager() {
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(); // Default font
        this.font.getData().setScale(2.0f); // Make text readable
    }

    /** call this at the start of the render loop */
    public void begin() {
        batch.begin();
    }

    /** call this at the end of the render loop */
    public void end() {
        batch.end();
    }

    /**
     * Draws a texture at the specified coordinates.
     * @param texture The image to draw
     * @param x X position
     * @param y Y position
     */
    public void draw(Texture texture, float x, float y) {
        batch.draw(texture, x, y);
    }

    /**
     * Draws a texture with specified size.
     * @param texture The image to draw
     * @param x X position
     * @param y Y position
     * @param width Target width
     * @param height Target height
     */
    public void draw(Texture texture, float x, float y, float width, float height) {
        batch.draw(texture, x, y, width, height);
    }

    /**
     * Draws a texture with specified size and tint color.
     * Useful for debug overlays (hitboxes, bounding boxes, etc.).
     */
    public void drawTinted(Texture texture, float x, float y, float width, float height, Color tint) {
        if (texture == null || tint == null) return;

        Color prev = batch.getColor();
        float pr = prev.r, pg = prev.g, pb = prev.b, pa = prev.a;
        batch.setColor(tint);
        batch.draw(texture, x, y, width, height);
        batch.setColor(pr, pg, pb, pa);

    }

    /**
     * Draws text to the screen.
     * @param text The string to display
     * @param x X position
     * @param y Y position
     */
    public void drawText(String text, float x, float y) {
        font.draw(batch, text, x, y);
    }

    // Add this method inside your existing OutputManager class
    public void clearScreen(float r, float g, float b, float a) {
        com.badlogic.gdx.utils.ScreenUtils.clear(r, g, b, a);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
    
    // Helper to get raw batch if absolutely needed (try to avoid using this in Game Scenes)
    public SpriteBatch getBatch() {
        return batch;
    }


}