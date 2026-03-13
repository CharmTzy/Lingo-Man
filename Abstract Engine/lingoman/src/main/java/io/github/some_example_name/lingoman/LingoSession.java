package io.github.some_example_name.lingoman;

import java.util.Random;

import io.github.some_example_name.lingoman.model.GameState;

public final class LingoSession {

    private static final LingoSession INSTANCE = new LingoSession();

    private final GameState gameState = new GameState();
    private final Random random = new Random();

    private LingoSession() {
    }

    public static LingoSession get() {
        return INSTANCE;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Random getRandom() {
        return random;
    }
}