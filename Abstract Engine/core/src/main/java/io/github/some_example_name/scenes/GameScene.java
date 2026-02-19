package io.github.some_example_name.scenes;

import java.util.IdentityHashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.entity.ChasingBoxEntity;
import io.github.some_example_name.entity.ControlledBoxEntity;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.managers.AudioManager;

public class GameScene implements Scene {

    private static final float WORLD_WIDTH = 640f;
    private static final float WORLD_HEIGHT = 480f;

    private EngineContext context;
    private final EntityManager entityManager = new EntityManager();
    private final Map<Entity, Boolean> borderCollisionStates = new IdentityHashMap<>();

    private ControlledBoxEntity player;
    private ChasingBoxEntity enemy;
    private Texture playerTexture;
    private Texture enemyTexture;
    private boolean playerCollisionActive;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
        playerTexture = createSolidTexture(Color.YELLOW);
        enemyTexture = createSolidTexture(Color.RED);

        player = new ControlledBoxEntity(playerTexture, context.getInputManager(), 80f, 80f);
        enemy = new ChasingBoxEntity(enemyTexture, player, 500f, 360f);

        entityManager.add(player);
        entityManager.add(enemy);
    }

    @Override
    public void enter() {
        context.getAudioManager().playMusic(AudioManager.BGM_GAME, true);
        resetState();
        System.out.println("[GameScene] Game started");
    }

    @Override
    public void exit() {
        System.out.println("[GameScene] Game paused/ended");
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().isPauseJustPressed()) {
            context.getAudioManager().playSound(AudioManager.SFX_MENU_NAVIGATE, false);
            context.getSceneManager().setActiveScene(SceneId.PAUSE);
        }

        if (context.getInputManager().isActionJustPressed()) {
            context.getSceneManager().setActiveScene(SceneId.GAME_OVER);
        }
    }

    @Override
    public void update(float deltaTime) {
        entityManager.update(deltaTime);

        for (Entity entity : entityManager.getAll()) {
            if (!entity.isActive()) {
                continue;
            }
            handleBorderCollision(entity);
        }

        boolean collidingWithPlayer = false;
        for (Entity entity : entityManager.getAll()) {
            if (!entity.isActive() || entity == player) {
                continue;
            }
            if (player.bounds().overlaps(entity.bounds())) {
                collidingWithPlayer = true;
                break;
            }
        }

        if (collidingWithPlayer && !playerCollisionActive) {
            context.getAudioManager().playSound(AudioManager.SFX_PLAYER_COLLISION, false);
        }
        playerCollisionActive = collidingWithPlayer;
    }

    @Override
    public void render() {
        context.getOutputManager().clearScreen(0.05f, 0.15f, 0.07f, 1f);
        entityManager.render(context.getOutputManager());
        context.getOutputManager().drawText("Move: WASD/Arrows", 16f, 464f);
        context.getOutputManager().drawText("ESC: Pause   SPACE: Game Over", 16f, 432f);
    }
    
    @Override
    public void dispose() {
        if (playerTexture != null) {
            playerTexture.dispose();
        }
        if (enemyTexture != null) {
            enemyTexture.dispose();
        }
        System.out.println("[GameScene] Resources disposed");
    }

    private Texture createSolidTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void resetState() {
        player.setX(80f);
        player.setY(80f);
        enemy.setX(500f);
        enemy.setY(360f);
        playerCollisionActive = false;
        borderCollisionStates.clear();
    }

    private void handleBorderCollision(Entity entity) {
        boolean collided = false;

        if (entity.getX() < 0f) {
            entity.setX(0f);
            collided = true;
        }
        if (entity.getY() < 0f) {
            entity.setY(0f);
            collided = true;
        }
        if (entity.getX() > WORLD_WIDTH - entity.getWidth()) {
            entity.setX(WORLD_WIDTH - entity.getWidth());
            collided = true;
        }
        if (entity.getY() > WORLD_HEIGHT - entity.getHeight()) {
            entity.setY(WORLD_HEIGHT - entity.getHeight());
            collided = true;
        }

        boolean wasColliding = borderCollisionStates.getOrDefault(entity, false);
        if (collided && !wasColliding) {
            context.getAudioManager().playSound(AudioManager.SFX_BORDER_COLLISION, false);
        }
        borderCollisionStates.put(entity, collided);
    }
}
