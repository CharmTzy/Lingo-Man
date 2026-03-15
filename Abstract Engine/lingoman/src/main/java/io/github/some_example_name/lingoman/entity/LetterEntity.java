package io.github.some_example_name.lingoman.entity;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.managers.OutputManager;

public class LetterEntity extends Entity {

    private final char letter;
    private final Color color = new Color(0.95f, 0.90f, 0.20f, 1f);

    public LetterEntity(String id, char letter, float x, float y, float size) {
        super(id);
        this.letter = letter;
        setX(x);
        setY(y);
        setWidth(size);
        setHeight(size);
    }

    public char getLetter() {
        return letter;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public boolean isTrigger() {
        return true;
    }

    @Override
    public void render(OutputManager outputManager) {
        outputManager.drawRect(getX(), getY(), getWidth(), getHeight(), color);
        float textScale = Math.max(0.42f, Math.min(0.68f, getWidth() / 22f));
        outputManager.drawTextCenteredScaled(
            String.valueOf(letter),
            getX() + getWidth() * 0.5f,
            getY() + getHeight() * 0.76f,
            Color.WHITE,
            textScale
        );
    }
}
