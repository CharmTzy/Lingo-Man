package io.github.some_example_name.lingoman.scenes;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.collision.ICollisionListener;
import io.github.some_example_name.lingoman.LingoAudio;
import io.github.some_example_name.lingoman.LingoInputActions;
import io.github.some_example_name.lingoman.LingoSceneIds;
import io.github.some_example_name.lingoman.LingoSaveFiles;
import io.github.some_example_name.lingoman.LingoSession;
import io.github.some_example_name.lingoman.entity.BossFireballEntity;
import io.github.some_example_name.lingoman.entity.FreezePickupEntity;
import io.github.some_example_name.lingoman.entity.GhostEntity;
import io.github.some_example_name.lingoman.entity.LetterEntity;
import io.github.some_example_name.lingoman.entity.PlayerEntity;
import io.github.some_example_name.lingoman.entity.ShockPickupEntity;
import io.github.some_example_name.lingoman.entity.WallBombEntity;
import io.github.some_example_name.lingoman.entity.WallEntity;
import io.github.some_example_name.lingoman.graphics.LingoSprites;
import io.github.some_example_name.lingoman.level.LevelBuilder;

import io.github.some_example_name.lingoman.level.MazeLayout;
import io.github.some_example_name.lingoman.model.GameState;
import io.github.some_example_name.lingoman.model.WordBank;
import io.github.some_example_name.lingoman.world.GhostDirector;
import io.github.some_example_name.lingoman.world.LingoWorld;
import io.github.some_example_name.scenes.Scene;

public class GameScene implements Scene {

    private static final Color BACKGROUND_OUTER = new Color(0.01f, 0.03f, 0.10f, 1f);
    private static final Color MAP_BAND_0 = new Color(0.02f, 0.09f, 0.28f, 1f);
    private static final Color MAP_BAND_1 = new Color(0.02f, 0.12f, 0.34f, 1f);
    private static final Color MAP_BAND_2 = new Color(0.03f, 0.15f, 0.39f, 1f);
    private static final Color MAP_BAND_3 = new Color(0.04f, 0.18f, 0.44f, 1f);
    private static final Color MAP_BAND_4 = new Color(0.05f, 0.21f, 0.49f, 1f);
    private static final Color MAP_BAND_5 = new Color(0.06f, 0.24f, 0.54f, 1f);
    private static final Color MAP_GRID_MINOR = new Color(0.31f, 0.92f, 1.00f, 0.17f);
    private static final Color MAP_GRID_MAJOR = new Color(0.58f, 0.98f, 1.00f, 0.30f);
    private static final Color MAP_CORE_GLOW = new Color(0.20f, 0.86f, 1.00f, 0.13f);
    private static final Color MAP_ACCENT_GOLD = new Color(1.00f, 0.80f, 0.28f, 0.11f);
    private static final Color MAP_WARM_STREAK = new Color(1.00f, 0.64f, 0.18f, 0.06f);

    private static final Color TEXT_PRIMARY = new Color(0.96f, 0.96f, 0.92f, 1f);
    private static final Color TEXT_MUTED = new Color(0.72f, 0.77f, 0.79f, 1f);
    private static final Color TEXT_ACCENT = new Color(0.98f, 0.84f, 0.36f, 1f);
    private static final Color TEXT_WARNING = new Color(1.00f, 0.64f, 0.47f, 1f);

    private static final float STATUS_MESSAGE_DURATION = 2.0f;
    private static final float RESPAWN_IMMUNITY_SECONDS = 5.0f;
    private static final float FREEZE_SECONDS = 4.5f;
    private static final float SHOCK_SECONDS = 6.0f;

    private EngineContext context;
    private final LingoWorld world = new LingoWorld();
    private GhostDirector ghostDirector;
    private LevelBuilder levelBuilder;

    private MazeLayout.Layout currentLayout;
    private PlayerEntity player;
    private FreezePickupEntity freezePickup;
    private ShockPickupEntity shockPickup;
    private float invulnerableTimer = 0f;
    private String statusMessage = "";
    private float statusMessageTimer = 0f;
    private boolean moveLoopPlaying = false;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
        this.ghostDirector = new GhostDirector(
            world,
            WallBombCollisionListener::new,
            BossFireballCollisionListener::new
        );
    }

    @Override
    public void enter() {
        try {
            if (LingoSession.get().consumeGameResumeRequest()) {
                moveLoopPlaying = false;
                context.getAudioManager().resumeSuspendedMusic();
                System.out.println("[LingoMan] Game resumed");
                return;
            }

            startNewGame();
            moveLoopPlaying = false;
            context.getAudioManager().playMusic(LingoAudio.BGM_GAME, true);
            System.out.println("[LingoMan] Game started");
        } catch (Throwable throwable) {
            stopGameplayAudio();
            System.out.println("[LingoMan] Failed to enter game scene: " + throwable.getMessage());
            if (Gdx.app != null) {
                Gdx.app.error("LingoMan", "Failed to enter game scene", throwable);
            }
            context.getSceneManager().setActiveScene(LingoSceneIds.MENU);
        }
    }

    @Override
    public void exit() {
        stopMovementAudio();
        System.out.println("[LingoMan] Game exit");
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().isActionJustPressed(LingoInputActions.GAME_MENU)) {
            context.getAudioManager().suspendMusic();
            context.getSceneManager().setActiveScene(LingoSceneIds.PAUSE);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (statusMessageTimer > 0f) {
            statusMessageTimer = Math.max(0f, statusMessageTimer - deltaTime);
            if (statusMessageTimer == 0f) {
                statusMessage = "";
            }
        }
        if (invulnerableTimer > 0f) {
            invulnerableTimer = Math.max(0f, invulnerableTimer - deltaTime);
        }

        world.update(deltaTime);
        ghostDirector.updateShooting(deltaTime, player, invulnerableTimer, currentLayout);
        ghostDirector.cleanupInactiveAttacks();
        updateMovementAudio();

        GameState state = LingoSession.get().getGameState();
        if (state.hasCollectedAllLetters()) {
            stopGameplayAudio();
            state.addFoundWord(state.getTargetWord());
            context.getSaveManager().save(LingoSaveFiles.PROFILE);
            state.setLastResult("WORD COMPLETE");
            context.getAudioManager().playSound(LingoAudio.SFX_VICTORY, false);
            context.getSceneManager().setActiveScene(LingoSceneIds.GAME_OVER);
        } else if (state.getLives() <= 0) {
            stopGameplayAudio();
            state.setLastResult("OUT OF LIVES");
            context.getAudioManager().playSound(LingoAudio.SFX_GAME_OVER, false);
            context.getSceneManager().setActiveScene(LingoSceneIds.GAME_OVER);
        }
    }

    @Override
    public void render() {
        context.getOutputManager().clearScreen(
            BACKGROUND_OUTER.r, BACKGROUND_OUTER.g, BACKGROUND_OUTER.b, BACKGROUND_OUTER.a);
        drawMapBackdrop();
        world.render(context.getOutputManager());
 
        GameState state = LingoSession.get().getGameState();
 
        final float ICON_SIZE = 40f;
        final float ICON_X    = 28f;
        final float ICON_Y    = 432f;   // sits above "Progress" line at y=454
 
        context.getOutputManager().draw(
            LingoSprites.wordIcon(state.getTargetWord()),
            ICON_X, ICON_Y, ICON_SIZE, ICON_SIZE
        );
 
        final float TEXT_X = ICON_X + ICON_SIZE + 8f;  // 76f
 
        context.getOutputManager().drawTextWithShadow(
            "Progress: " + state.getCollectedLettersDisplay(), TEXT_X, 474f, TEXT_PRIMARY);
 
        char nextLetter = state.getNextExpectedLetter();
        String nextHint = nextLetter == '\0' ? "Next: -" : "Next: " + nextLetter;
        context.getOutputManager().drawTextWithShadow(nextHint, TEXT_X, 454f, TEXT_WARNING);
 
        context.getOutputManager().drawTextWithShadow("Lives: " + state.getLives(), 432f, 474f,
            state.getLives() <= 1 ? TEXT_WARNING : TEXT_PRIMARY);
        context.getOutputManager().drawTextWithShadow(
            "Mode: " + state.getDifficulty(), 432f, 454f, TEXT_PRIMARY);
 
        context.getOutputManager().drawTextWithShadow("Move: WASD / Arrows", 28f, 18f, TEXT_PRIMARY);
        context.getOutputManager().drawTextWithShadow("Dash: SPACE", 246f, 18f, TEXT_PRIMARY);
        context.getOutputManager().drawTextRightAlignedWithShadow("Menu: M or ESC", 610f, 18f, TEXT_MUTED);
 
        if (!statusMessage.isBlank()) {
            context.getOutputManager().drawTextCenteredScaled(
                statusMessage, 320f, 474f, TEXT_WARNING, 0.8f);
        }
    }

    private void drawMapBackdrop() {
        if (currentLayout == null) {
            return;
        }

        float left = currentLayout.getOffsetX();
        float bottom = currentLayout.getOffsetY();
        float width = currentLayout.getCols() * currentLayout.getTileSize();
        float height = currentLayout.getRows() * currentLayout.getTileSize();
        float bandHeight = height / 6f;

        context.getOutputManager().drawRect(left, bottom + bandHeight * 0f, width, bandHeight + 1f, MAP_BAND_0);
        context.getOutputManager().drawRect(left, bottom + bandHeight * 1f, width, bandHeight + 1f, MAP_BAND_1);
        context.getOutputManager().drawRect(left, bottom + bandHeight * 2f, width, bandHeight + 1f, MAP_BAND_2);
        context.getOutputManager().drawRect(left, bottom + bandHeight * 3f, width, bandHeight + 1f, MAP_BAND_3);
        context.getOutputManager().drawRect(left, bottom + bandHeight * 4f, width, bandHeight + 1f, MAP_BAND_4);
        context.getOutputManager().drawRect(left, bottom + bandHeight * 5f, width, bandHeight + 1f, MAP_BAND_5);

        context.getOutputManager().drawRect(
            left + width * 0.16f,
            bottom + height * 0.12f,
            width * 0.68f,
            height * 0.70f,
            MAP_CORE_GLOW
        );
        context.getOutputManager().drawRect(
            left + width * 0.24f,
            bottom + height * 0.22f,
            width * 0.52f,
            height * 0.48f,
            MAP_ACCENT_GOLD
        );
        context.getOutputManager().drawRect(
            left + width * 0.10f,
            bottom + height * 0.79f,
            width * 0.80f,
            height * 0.08f,
            MAP_WARM_STREAK
        );
        context.getOutputManager().drawRect(
            left + width * 0.18f,
            bottom + height * 0.09f,
            width * 0.64f,
            height * 0.06f,
            MAP_WARM_STREAK
        );

        int cols = currentLayout.getCols();
        int rows = currentLayout.getRows();
        float tile = currentLayout.getTileSize();

        for (int col = 0; col <= cols; col++) {
            float x = left + col * tile;
            boolean major = (col % 4) == 0;
            float lineWidth = major ? 1.8f : 0.9f;
            context.getOutputManager().drawRect(
                x - lineWidth * 0.5f,
                bottom,
                lineWidth,
                height,
                major ? MAP_GRID_MAJOR : MAP_GRID_MINOR
            );
        }

        for (int row = 0; row <= rows; row++) {
            float y = bottom + row * tile;
            boolean major = (row % 4) == 0;
            float lineHeight = major ? 1.8f : 0.9f;
            context.getOutputManager().drawRect(
                left,
                y - lineHeight * 0.5f,
                width,
                lineHeight,
                major ? MAP_GRID_MAJOR : MAP_GRID_MINOR
            );
        }
    }

    @Override
    public void dispose() {
        stopGameplayAudio();
        clearWorld();
        System.out.println("[LingoMan] Game disposed");
    }

    private void startNewGame() {
        GameState state = LingoSession.get().getGameState();
        Random rng = LingoSession.get().getRandom();

        state.resetLives();
        state.setLastResult("");
        state.setTargetWord(WordBank.randomWord(state.getDifficulty(), rng));

        currentLayout = MazeLayout.forDifficulty(state.getDifficulty());
        invulnerableTimer = 0f;
        statusMessage = "";
        statusMessageTimer = 0f;

        buildLevel(state);
    }

    private void buildLevel(GameState state) {
        clearWorld();
        levelBuilder = new LevelBuilder(world, currentLayout, LingoSession.get().getRandom());
        levelBuilder.buildWalls();
        player = levelBuilder.buildPlayer(
            context.getInputManager(),
            playerSpeed(),
            entitySize(),
            new PlayerCollisionListener()
        );
        ghostDirector.setBombDropCells(levelBuilder.collectBombDropCells());
        ghostDirector.buildGhosts(currentLayout, state.getDifficulty(), player, ghostSpeed(state.getDifficulty()), entitySize());

        List<GridPoint2> itemCells = levelBuilder.collectItemCells();
        levelBuilder.buildLetters(state.getTargetWord(), itemCells, letterSize());

        float pickupSize = Math.max(currentLayout.getTileSize() * 0.62f, 14f);
        freezePickup = levelBuilder.buildFreezePickup(itemCells, pickupSize);
        shockPickup = levelBuilder.buildShockPickup(itemCells, pickupSize);
    }

    private void clearWorld() {
        freezePickup = null;
        shockPickup = null;
        player = null;
        if (ghostDirector != null) {
            ghostDirector.clear();
        }
        world.clear();
    }

    private void resetPositions() {
        GridPoint2 playerSpawn = currentLayout.getPlayerSpawn();
        player.setX(MazeLayout.toWorldXCentered(currentLayout, playerSpawn.x, player.getWidth()));
        player.setY(MazeLayout.toWorldYCentered(currentLayout, playerSpawn.y, player.getHeight()));
        player.setVx(0f);
        player.setVy(0f);
        player.clearDash();
        player.clearShock();
        player.clearRespawnProtection();
        player.setRespawnProtection(RESPAWN_IMMUNITY_SECONDS);
        ghostDirector.resetAfterPlayerHit(
            currentLayout,
            player,
            ghostSpeed(LingoSession.get().getGameState().getDifficulty())
        );
    }

    private void respawnGhost(GhostEntity ghost) {
        ghostDirector.respawnGhost(
            ghost,
            currentLayout,
            player,
            ghostSpeed(LingoSession.get().getGameState().getDifficulty()),
            RESPAWN_IMMUNITY_SECONDS
        );
    }

    private void handlePlayerHit() {
        if (invulnerableTimer > 0f) {
            return;
        }

        GameState state = LingoSession.get().getGameState();
        state.loseLife();
        invulnerableTimer = RESPAWN_IMMUNITY_SECONDS;
        showStatus("Ouch! Lives left: " + state.getLives());
        context.getAudioManager().playSound(LingoAudio.SFX_HURT, false);
        resetPositions();
    }

    private void updateMovementAudio() {
        boolean shouldPlayMoveLoop = isMovementInputPressed();
        if (shouldPlayMoveLoop == moveLoopPlaying) {
            return;
        }

        moveLoopPlaying = shouldPlayMoveLoop;
        if (moveLoopPlaying) {
            context.getAudioManager().playLoopingSound(LingoAudio.SFX_MOVE);
        } else {
            context.getAudioManager().stopLoopingSound(LingoAudio.SFX_MOVE);
        }
    }

    private boolean isMovementInputPressed() {
        return (player != null && player.isDashing())
            || context.getInputManager().isActionPressed(LingoInputActions.MOVE_LEFT)
            || context.getInputManager().isActionPressed(LingoInputActions.MOVE_RIGHT)
            || context.getInputManager().isActionPressed(LingoInputActions.MOVE_UP)
            || context.getInputManager().isActionPressed(LingoInputActions.MOVE_DOWN);
    }

    private void stopMovementAudio() {
        moveLoopPlaying = false;
        context.getAudioManager().stopLoopingSound(LingoAudio.SFX_MOVE);
    }

    private void stopGameplayAudio() {
        stopMovementAudio();
        context.getAudioManager().stopMusic();
    }

    private void showStatus(String message) {
        statusMessage = message == null ? "" : message;
        statusMessageTimer = STATUS_MESSAGE_DURATION;
    }

    private float entitySize() {
        return currentLayout.getTileSize() * 0.78f;
    }

    private float letterSize() {
        return currentLayout.getTileSize() * 0.55f;
    }

    private float playerSpeed() {
        return currentLayout.getTileSize() * 5.0f;
    }

    private float ghostSpeed(GameState.Difficulty difficulty) {
        float scale = switch (difficulty) {
            case MEDIUM -> 3.0f;
            case HARD -> 3.35f;
            default -> 2.6f;
        };
        return currentLayout.getTileSize() * scale;
    }

    private void activateFreeze() {
        for (GhostEntity ghost : ghostDirector.getGhosts()) {
            ghost.freezeFor(FREEZE_SECONDS);
        }
        showStatus("Freeze!");
    }

    private void activateShock() {
        if (player == null) {
            return;
        }
        player.activateShock(SHOCK_SECONDS);
        showStatus("Lightning!");
    }

    private void handlePlayerGhostContact(GhostEntity ghost) {
        if (ghost == null || !ghost.isActive()) {
            return;
        }
        if (ghost.isRespawnProtected() || (player != null && player.isRespawnProtected())) {
            return;
        }
        if (player != null && player.hasShockPower()) {
            if (!ghost.isRespawnProtected()) {
                respawnGhost(ghost);
                showStatus(ghost.isBoss() ? "Boss electrocuted!" : "Ghost electrocuted!");
            }
            return;
        }
        if (ghost.isFrozen()) {
            return;
        }
        handlePlayerHit();
    }

    private final class PlayerCollisionListener implements ICollisionListener {

        @Override
        public void onCollisionEnter(Collider other) {
            if (other == null) {
                return;
            }

            Object owner = other.getOwner();
            if (owner instanceof LetterEntity letter) {
                if (!letter.isActive()) {
                    return;
                }

                GameState state = LingoSession.get().getGameState();
                char picked = Character.toUpperCase(letter.getLetter());

                if (state.collectNextLetter(picked)) {
                    context.getAudioManager().playSound(LingoAudio.SFX_COLLECT_LETTER, false);
                    world.removeEntity(letter);
                    showStatus("Correct: " + picked);
                } else {
                    context.getAudioManager().playSound(LingoAudio.SFX_WRONG_LETTER, false);
                    letter.triggerWrongFlash();

                    char expected = state.getNextExpectedLetter();
                    if (expected != '\0') {
                        showStatus("Wrong letter, find: " + expected);
                    } else {
                        showStatus("Wrong letter");
                    }
                }
            } else if (owner instanceof FreezePickupEntity pickup) {
                if (!pickup.isActive()) {
                    return;
                }

                world.removeEntity(pickup);
                freezePickup = null;
                activateFreeze();
            } else if (owner instanceof ShockPickupEntity pickup) {
                if (!pickup.isActive()) {
                    return;
                }

                world.removeEntity(pickup);
                shockPickup = null;
                activateShock();
            } else if (owner instanceof GhostEntity ghost) {
                handlePlayerGhostContact(ghost);
            }
        }

        @Override
        public void onCollisionStay(Collider other) {
            if (other == null) {
                return;
            }
            Object owner = other.getOwner();
            if (owner instanceof GhostEntity ghost) {
                handlePlayerGhostContact(ghost);
            }
        }

        @Override
        public void onCollisionExit(Collider other) {
        }
    }

    private final class WallBombCollisionListener implements ICollisionListener {

        private final WallBombEntity bomb;

        private WallBombCollisionListener(WallBombEntity bomb) {
            this.bomb = bomb;
        }

        @Override
        public void onCollisionEnter(Collider other) {
            handleBombContact(other);
        }

        @Override
        public void onCollisionStay(Collider other) {
            handleBombContact(other);
        }

        private void handleBombContact(Collider other) {
            if (other == null || bomb == null || !bomb.isActive()) {
                return;
            }
            if (!bomb.isExploding()) {
                return;
            }

            Object owner = other.getOwner();
            if (owner instanceof PlayerEntity) {
                ghostDirector.removeWallBomb(bomb);
                handlePlayerHit();
                return;
            }
            if (!(owner instanceof GhostEntity ghost)) {
                return;
            }
            if (ghost.getType() != GhostEntity.GhostType.NORMAL
                && ghost.getType() != GhostEntity.GhostType.HELL_HOUND
                && ghost.getType() != GhostEntity.GhostType.BOSS) {
                return;
            }
            if (ghost.isRespawnProtected()) {
                return;
            }

            respawnGhost(ghost);
            ghostDirector.removeWallBomb(bomb);
            showStatus(ghost.isBoss() ? "Boss hit by bomb!" : "Ghost hit by bomb!");
        }

        @Override
        public void onCollisionExit(Collider other) {
        }
    }

    private final class BossFireballCollisionListener implements ICollisionListener {

        private final BossFireballEntity fireball;

        private BossFireballCollisionListener(BossFireballEntity fireball) {
            this.fireball = fireball;
        }

        @Override
        public void onCollisionEnter(Collider other) {
            handleFireballContact(other);
        }

        @Override
        public void onCollisionStay(Collider other) {
            handleFireballContact(other);
        }

        private void handleFireballContact(Collider other) {
            if (other == null || fireball == null || !fireball.isActive()) {
                return;
            }

            Object owner = other.getOwner();
            if (owner instanceof WallEntity) {
                ghostDirector.removeBossFireball(fireball);
                return;
            }
            if (owner instanceof PlayerEntity) {
                ghostDirector.removeBossFireball(fireball);
                handlePlayerHit();
            }
        }

        @Override
        public void onCollisionExit(Collider other) {
        }
    }
}
