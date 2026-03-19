package io.github.some_example_name.lingoman;

import java.util.Random;

import io.github.some_example_name.lingoman.model.GameState;

public final class LingoSession {

    private static final LingoSession INSTANCE = new LingoSession();

    private final GameState gameState = new GameState();
    private final Random random = new Random();
    private boolean resumeGameRequested;
    private String settingsReturnSceneId = LingoSceneIds.MENU;

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

    public void requestGameResume() {
        resumeGameRequested = true;
    }

    public boolean consumeGameResumeRequest() {
        boolean requested = resumeGameRequested;
        resumeGameRequested = false;
        return requested;
    }

    public void setSettingsReturnSceneId(String sceneId) {
        if (sceneId != null && !sceneId.isBlank()) {
            settingsReturnSceneId = sceneId;
        }
    }

    public String getSettingsReturnSceneId() {
        return settingsReturnSceneId;
    }
}
