package io.github.some_example_name.scenes;

import java.util.IdentityHashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import io.github.some_example_name.EngineContext;
import io.github.some_example_name.entity.ChasingBoxEntity;
import io.github.some_example_name.entity.ControlledBoxEntity;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.managers.AudioManager;
import io.github.some_example_name.managers.CollisionManager;
import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.collision.EntityCollisionListenerAdapter;
import io.github.some_example_name.collision.ICollisionListener;
import io.github.some_example_name.debug.DebugDraw;
import io.github.some_example_name.debug.CollisionDebugOverlay;
import io.github.some_example_name.save.ISaveable;
import io.github.some_example_name.save.SaveData;

public class GameScene implements Scene, ISaveable {

    private static final float WORLD_WIDTH = 640f;
    private static final float WORLD_HEIGHT = 480f;
    private static final String SESSION_FILE = "session.json";

    private EngineContext context;
    private final EntityManager entityManager = new EntityManager();
    private final CollisionManager collisionManager = new CollisionManager();
    private final Map<Entity, Boolean> borderCollisionStates = new IdentityHashMap<>();

    private ControlledBoxEntity player;
    private ChasingBoxEntity enemy;
    private Collider playerCollider;
    private Collider enemyCollider;
    private boolean showCollisionDebug = false;
    private DebugDraw debugDraw;
    private CollisionDebugOverlay collisionDebugOverlay;

    private Texture playerTexture;
    private Texture enemyTexture;

    @Override
    public void initialize(EngineContext context) {
        this.context = context;
        playerTexture = new Texture(Gdx.files.internal("box.png"));
        enemyTexture = new Texture(Gdx.files.internal("monster.png"));

        player = new ControlledBoxEntity(playerTexture, context.getInputManager(), 80f, 80f);
        enemy = new ChasingBoxEntity(enemyTexture, player, 500f, 360f);

        entityManager.add(player);
        entityManager.add(enemy);

        // Collision setup (manager + colliders)
        playerCollider = new Collider(player, player.getWidth(), player.getHeight());
        // Use listener callbacks to trigger SFX once on collision enter.
        playerCollider.setListener(new ICollisionListener() {
            private final EntityCollisionListenerAdapter base = new EntityCollisionListenerAdapter(player);

            @Override
            public void onCollisionEnter(Collider other) {
                context.getAudioManager().playSound(AudioManager.SFX_PLAYER_COLLISION, false);
                base.onCollisionEnter(other);
            }

            @Override
            public void onCollisionStay(Collider other) {
                base.onCollisionStay(other);
            }

            @Override
            public void onCollisionExit(Collider other) {
                base.onCollisionExit(other);
            }
        });
        
        debugDraw = new DebugDraw();
        collisionDebugOverlay = new CollisionDebugOverlay(debugDraw);

        enemyCollider = new Collider(enemy, enemy.getWidth(), enemy.getHeight());
        enemyCollider.setListener(new EntityCollisionListenerAdapter(enemy));
        collisionManager.add(playerCollider);
        collisionManager.add(enemyCollider);
        context.getSaveManager().register(this);
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

        if (context.getInputManager().isNpcBehaviourToggleJustPressed()) {
            enemy.cycleBehaviourMode();
            context.getAudioManager().playSound(AudioManager.SFX_MENU_NAVIGATE, false);
        }

        if (context.getInputManager().isSaveSessionJustPressed()) {
            context.getSaveManager().save(SESSION_FILE);
            context.getAudioManager().playSound(AudioManager.SFX_MENU_NAVIGATE, false);
        }

        if (context.getInputManager().isLoadSessionJustPressed()) {
            context.getSaveManager().load(SESSION_FILE);
            context.getAudioManager().playSound(AudioManager.SFX_MENU_NAVIGATE, false);
        }

        if (context.getInputManager().isDeleteSessionJustPressed()) {
            context.getSaveManager().delete(SESSION_FILE);
            context.getAudioManager().playSound(AudioManager.SFX_MENU_NAVIGATE, false);
        }
        if (context.getInputManager().isToggleCollisionDebugJustPressed()) {
            showCollisionDebug = !showCollisionDebug;
        }

    }

    @Override
    public void update(float deltaTime) {
        entityManager.update(deltaTime);
        collisionManager.update();

        for (Entity entity : entityManager.getAll()) {
            if (!entity.isActive()) {
                continue;
            }
            handleBorderCollision(entity);
        }

    }

    @Override
    public void render() {
        context.getOutputManager().clearScreen(0.05f, 0.15f, 0.07f, 1f);
        entityManager.render(context.getOutputManager());

        if (showCollisionDebug) {
            collisionDebugOverlay.render(context.getOutputManager(), collisionManager);
            context.getOutputManager().drawText("Hitboxes: ON (F1)", 16f, 304f);
        } else {
            context.getOutputManager().drawText("Hitboxes: OFF (F1)", 16f, 304f);
        }
        context.getOutputManager().drawText("Move: WASD/Arrows", 16f, 416f);
        context.getOutputManager().drawText("TAB: NPC Mode: " + enemy.getBehaviourModeLabel(), 16f, 388f);
        context.getOutputManager().drawText("ESC: Pause   SPACE: Game Over", 16f, 360f);
        context.getOutputManager().drawText("F5: Save   F9: Load   F10: Delete", 16f, 332f);
    }
    
    @Override
    public void dispose() {
        context.getSaveManager().unregister(getSaveId());
        
        if (playerCollider != null) collisionManager.remove(playerCollider);
        if (enemyCollider != null) collisionManager.remove(enemyCollider);
        if (playerTexture != null) {
            playerTexture.dispose();
        }
        if (enemyTexture != null) {
            enemyTexture.dispose();
        }
        if (debugDraw != null) debugDraw.dispose();
        System.out.println("[GameScene] Resources disposed");
    }

    private void resetState() {
        player.setX(80f);
        player.setY(80f);
        enemy.setX(500f);
        enemy.setY(360f);
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

    @Override
    public String getSaveId() {
        return "game_scene";
    }

    @Override
    public SaveData writeSaveData() {
        SaveData data = new SaveData(getSaveId());
        data.put("player_x", player.getX());
        data.put("player_y", player.getY());
        data.put("enemy_x", enemy.getX());
        data.put("enemy_y", enemy.getY());
        data.put("enemy_mode", enemy.getBehaviourMode().name());
        return data;
    }

    @Override
    public void readSaveData(SaveData saveData) {
        if (saveData == null) {
            return;
        }

        player.setX(readFloat(saveData, "player_x", player.getX()));
        player.setY(readFloat(saveData, "player_y", player.getY()));
        enemy.setX(readFloat(saveData, "enemy_x", enemy.getX()));
        enemy.setY(readFloat(saveData, "enemy_y", enemy.getY()));

        Object modeObj = saveData.get("enemy_mode");
        if (modeObj instanceof String modeStr) {
            try {
                enemy.setBehaviourMode(ChasingBoxEntity.BehaviourMode.valueOf(modeStr));
            } catch (IllegalArgumentException ignored) {
                // Keep current mode for unknown save data.
            }
        }

        borderCollisionStates.clear();
        handleBorderCollision(player);
        handleBorderCollision(enemy);
    }

    private float readFloat(SaveData saveData, String key, float fallback) {
        Object value = saveData.get(key);
        if (value instanceof Number number) {
            return number.floatValue();
        }
        return fallback;
    }
}
