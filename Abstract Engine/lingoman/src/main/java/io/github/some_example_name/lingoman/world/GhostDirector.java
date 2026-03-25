package io.github.some_example_name.lingoman.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.collision.EntityCollisionListenerAdapter;
import io.github.some_example_name.collision.ICollisionListener;
import io.github.some_example_name.lingoman.LingoSession;
import io.github.some_example_name.lingoman.entity.BossFireballEntity;
import io.github.some_example_name.lingoman.entity.GhostEntity;
import io.github.some_example_name.lingoman.entity.PlayerEntity;
import io.github.some_example_name.lingoman.entity.WallBombEntity;
import io.github.some_example_name.lingoman.level.MazeLayout;
import io.github.some_example_name.lingoman.model.GameState;
import io.github.some_example_name.lingoman.movement.GhostMovementGuard;
import io.github.some_example_name.lingoman.movement.MazeSeekBehaviour;
import io.github.some_example_name.lingoman.movement.MazeWanderBehaviour;
import io.github.some_example_name.movement.behaviour.MovementBehaviour;

public final class GhostDirector {

    private enum GhostMovePattern {
        SEEK,
        WANDER,
        STATIONARY
    }

    private enum ShotPattern {
        NONE,
        BOMB_DROP,
        FIREBALL
    }

    private static final class GhostLoadout {
        private final GhostEntity.GhostType type;
        private final GhostMovePattern movePattern;
        private final Color color;
        private final float speedMultiplier;
        private final float sizeMultiplier;
        private final ShotPattern shotPattern;
        private final float shotCooldownSeconds;

        private GhostLoadout(
            GhostEntity.GhostType type,
            GhostMovePattern movePattern,
            Color color,
            float speedMultiplier,
            float sizeMultiplier,
            ShotPattern shotPattern,
            float shotCooldownSeconds
        ) {
            this.type = type;
            this.movePattern = movePattern;
            this.color = color == null ? new Color(1f, 0.3f, 0.3f, 1f) : new Color(color);
            this.speedMultiplier = speedMultiplier;
            this.sizeMultiplier = sizeMultiplier;
            this.shotPattern = shotPattern;
            this.shotCooldownSeconds = shotCooldownSeconds;
        }

        private boolean canShoot() {
            return shotPattern != ShotPattern.NONE;
        }
    }

    private static final float BOMB_THROW_SECONDS = 0.82f;
    private static final float BOMB_BLAST_SECONDS = 0.18f;
    private static final float FIREBALL_SPEED = 182f;
    private static final float FIREBALL_LIFETIME_SECONDS = 1.55f;
    private static final float FIREBALL_ATTACK_RANGE_TILES = 6.0f;
    private static final float BOMB_TARGET_PLAYER_CHANCE = 0.70f;
    private static final int BOMB_NEAR_PLAYER_RADIUS = 3;

    private final LingoWorld world;
    private final Function<WallBombEntity, ICollisionListener> wallBombListenerFactory;
    private final Function<BossFireballEntity, ICollisionListener> fireballListenerFactory;

    private final List<GhostEntity> ghosts = new ArrayList<>();
    private final List<WallBombEntity> wallBombs = new ArrayList<>();
    private final List<BossFireballEntity> bossFireballs = new ArrayList<>();
    private final Map<GhostEntity, Float> ghostShotCooldowns = new IdentityHashMap<>();
    private final Map<GhostEntity, GhostLoadout> ghostLoadouts = new IdentityHashMap<>();
    private final Map<GhostEntity, GridPoint2> ghostSpawnCells = new IdentityHashMap<>();
    private final List<GridPoint2> bombDropCells = new ArrayList<>();

    private int bombSequence = 0;
    private int fireballSequence = 0;

    public GhostDirector(
        LingoWorld world,
        Function<WallBombEntity, ICollisionListener> wallBombListenerFactory,
        Function<BossFireballEntity, ICollisionListener> fireballListenerFactory
    ) {
        if (world == null) {
            throw new IllegalArgumentException("world cannot be null");
        }
        this.world = world;
        this.wallBombListenerFactory = wallBombListenerFactory;
        this.fireballListenerFactory = fireballListenerFactory;
    }

    public List<GhostEntity> getGhosts() {
        return Collections.unmodifiableList(ghosts);
    }

    public void setBombDropCells(List<GridPoint2> cells) {
        bombDropCells.clear();
        if (cells != null) {
            bombDropCells.addAll(cells);
        }
    }

    public void clear() {
        ghosts.clear();
        wallBombs.clear();
        bossFireballs.clear();
        ghostShotCooldowns.clear();
        ghostLoadouts.clear();
        ghostSpawnCells.clear();
        bombDropCells.clear();
        bombSequence = 0;
        fireballSequence = 0;
    }

    public void buildGhosts(
        MazeLayout.Layout layout,
        GameState.Difficulty difficulty,
        PlayerEntity player,
        float baseSpeed,
        float baseSize
    ) {
        if (layout == null) {
            return;
        }

        ghosts.clear();
        wallBombs.clear();
        bossFireballs.clear();
        ghostShotCooldowns.clear();
        ghostLoadouts.clear();
        ghostSpawnCells.clear();
        bombSequence = 0;
        fireballSequence = 0;

        float speed = Math.max(0f, baseSpeed);
        GridPoint2[] spawns = layout.getGhostSpawns();
        List<GhostLoadout> loadouts = buildGhostLoadouts(difficulty);
        int pathSpawnIndex = 0;

        for (int i = 0; i < loadouts.size(); i++) {
            GhostLoadout loadout = loadouts.get(i);
            GridPoint2 spawn = loadout.type == GhostEntity.GhostType.WALL_BOMBER
                ? findWallBomberCell(layout)
                : spawns[pathSpawnIndex++ % spawns.length];

            float size = Math.max(4f, baseSize) * loadout.sizeMultiplier;
            float x = MazeLayout.toWorldXCentered(layout, spawn.x, size);
            float y = MazeLayout.toWorldYCentered(layout, spawn.y, size);
            GhostEntity ghost = new GhostEntity("ghost_" + i, loadout.type, loadout.color, x, y, size);
            ghosts.add(ghost);
            ghostLoadouts.put(ghost, loadout);
            ghostSpawnCells.put(ghost, new GridPoint2(spawn));

            if (loadout.type == GhostEntity.GhostType.WALL_BOMBER) {
                world.addEntity(ghost);
            } else {
                world.addCollidableEntity(ghost, new EntityCollisionListenerAdapter(ghost));
            }

            MovementBehaviour behaviour = buildGhostBehaviour(loadout, layout, player, speed);
            if (behaviour != null) {
                world.assignBehaviour(ghost, new GhostMovementGuard(behaviour));
            } else {
                ghost.setMovementBehaviour(null);
                ghost.setVx(0f);
                ghost.setVy(0f);
            }

            if (loadout.canShoot()) {
                ghostShotCooldowns.put(ghost, initialShotCooldown(loadout, ghostShotCooldowns.size()));
            }
        }
    }

    public void resetAfterPlayerHit(
        MazeLayout.Layout layout,
        PlayerEntity player,
        float baseSpeed
    ) {
        clearAttacks();
        for (GhostEntity ghost : ghosts) {
            resetGhostToSpawn(ghost, layout, player, baseSpeed);
        }
        resetGhostShotCooldowns();
    }

    public void respawnGhost(
        GhostEntity ghost,
        MazeLayout.Layout layout,
        PlayerEntity player,
        float baseSpeed,
        float respawnImmunitySeconds
    ) {
        resetGhostToSpawn(ghost, layout, player, baseSpeed);
        if (ghost != null) {
            ghost.setRespawnProtection(respawnImmunitySeconds);
        }

        GhostLoadout loadout = ghostLoadouts.get(ghost);
        if (loadout != null && loadout.canShoot()) {
            ghostShotCooldowns.put(ghost, shotCooldown(loadout));
        }
    }

    public void updateShooting(
        float deltaTime,
        PlayerEntity player,
        float invulnerableTimer,
        MazeLayout.Layout layout
    ) {
        if (ghostShotCooldowns.isEmpty() || invulnerableTimer > 0f || player == null) {
            return;
        }

        for (GhostEntity ghost : ghosts) {
            GhostLoadout loadout = ghostLoadouts.get(ghost);
            if (ghost == null || !ghost.isActive() || ghost.isFrozen() || loadout == null || !ghostShotCooldowns.containsKey(ghost)) {
                continue;
            }

            float remaining = ghostShotCooldowns.get(ghost) - deltaTime;
            if (remaining > 0f) {
                ghostShotCooldowns.put(ghost, remaining);
                continue;
            }

            boolean spawned = spawnGhostAttack(ghost, loadout, player, layout);
            ghostShotCooldowns.put(ghost, spawned ? shotCooldown(loadout) : 0.45f);
            if (spawned) {
                break;
            }
        }
    }

    public void cleanupInactiveAttacks() {
        cleanupInactiveBombs();
        cleanupInactiveFireballs();
    }

    public void removeWallBomb(WallBombEntity bomb) {
        if (bomb == null) {
            return;
        }
        bomb.expire();
        wallBombs.remove(bomb);
        world.removeEntity(bomb);
    }

    public void removeBossFireball(BossFireballEntity fireball) {
        if (fireball == null) {
            return;
        }
        fireball.expire();
        bossFireballs.remove(fireball);
        world.removeEntity(fireball);
    }

    private void resetGhostToSpawn(
        GhostEntity ghost,
        MazeLayout.Layout layout,
        PlayerEntity player,
        float baseSpeed
    ) {
        if (ghost == null || layout == null) {
            return;
        }

        GridPoint2 spawn = ghostSpawnCells.get(ghost);
        if (spawn == null) {
            return;
        }

        ghost.setX(MazeLayout.toWorldXCentered(layout, spawn.x, ghost.getWidth()));
        ghost.setY(MazeLayout.toWorldYCentered(layout, spawn.y, ghost.getHeight()));
        ghost.setVx(0f);
        ghost.setVy(0f);
        ghost.clearFreeze();
        ghost.clearThrowAnimation();
        ghost.clearRespawnProtection();
        refreshGhostBehaviour(ghost, layout, player, baseSpeed);
    }

    private void refreshGhostBehaviour(
        GhostEntity ghost,
        MazeLayout.Layout layout,
        PlayerEntity player,
        float baseSpeed
    ) {
        if (ghost == null || layout == null) {
            return;
        }

        MovementBehaviour behaviour = buildGhostBehaviour(ghostLoadouts.get(ghost), layout, player, baseSpeed);
        if (behaviour != null) {
            world.assignBehaviour(ghost, new GhostMovementGuard(behaviour));
        } else {
            ghost.setMovementBehaviour(null);
            ghost.setVx(0f);
            ghost.setVy(0f);
        }
    }

    private MovementBehaviour buildGhostBehaviour(
        GhostLoadout loadout,
        MazeLayout.Layout layout,
        PlayerEntity player,
        float baseSpeed
    ) {
        float adjustedSpeed = Math.max(0f, baseSpeed) * (loadout == null ? 1f : loadout.speedMultiplier);
        GhostMovePattern pattern = loadout == null ? GhostMovePattern.SEEK : loadout.movePattern;
        if (pattern == GhostMovePattern.STATIONARY) {
            return null;
        }
        if (pattern == GhostMovePattern.WANDER || player == null) {
            return new MazeWanderBehaviour(layout, adjustedSpeed);
        }
        return new MazeSeekBehaviour(layout, player, adjustedSpeed);
    }

    private boolean spawnGhostAttack(GhostEntity ghost, GhostLoadout loadout, PlayerEntity player, MazeLayout.Layout layout) {
        if (ghost == null || loadout == null || !loadout.canShoot() || player == null || layout == null) {
            return false;
        }
        if (loadout.shotPattern == ShotPattern.BOMB_DROP) {
            return spawnWallBomb(ghost, player, layout);
        }
        if (loadout.shotPattern == ShotPattern.FIREBALL) {
            return spawnBossFireball(ghost, player, layout);
        }
        return false;
    }

    private boolean spawnWallBomb(GhostEntity bomber, PlayerEntity player, MazeLayout.Layout layout) {
        if (bomber == null || layout == null || bombDropCells.isEmpty() || hasActiveWallBomb()) {
            return false;
        }

        GridPoint2 targetCell = selectBombTargetCell(layout, player);
        if (targetCell == null) {
            return false;
        }

        float bombSize = Math.max(layout.getTileSize() * 0.34f, 9f);
        float blastSize = Math.max(layout.getTileSize() * 0.84f, 16f);
        Vector2 targetCenter = MazeLayout.toCellCenter(layout, targetCell);
        Vector2 throwOrigin = bomber.getThrowOrigin(targetCenter.x, targetCenter.y);
        float startCenterX = throwOrigin.x;
        float startCenterY = throwOrigin.y;

        bomber.playThrowAnimation(BOMB_THROW_SECONDS, targetCenter.x, targetCenter.y);

        WallBombEntity bomb = new WallBombEntity(
            "wall_bomb_" + bombSequence++,
            startCenterX,
            startCenterY,
            targetCenter.x,
            targetCenter.y,
            bombSize,
            blastSize,
            BOMB_THROW_SECONDS,
            BOMB_BLAST_SECONDS
        );
        wallBombs.add(bomb);
        world.addCollidableEntity(bomb, createWallBombListener(bomb));
        return true;
    }

    private boolean spawnBossFireball(GhostEntity boss, PlayerEntity player, MazeLayout.Layout layout) {
        if (boss == null || player == null || layout == null || hasActiveBossFireball()) {
            return false;
        }

        float playerCenterX = player.getX() + player.getWidth() * 0.5f;
        float playerCenterY = player.getY() + player.getHeight() * 0.5f;
        float bossCenterX = boss.getX() + boss.getWidth() * 0.5f;
        float bossCenterY = boss.getY() + boss.getHeight() * 0.5f;
        Vector2 direction = new Vector2(playerCenterX - bossCenterX, playerCenterY - bossCenterY);
        float targetDistance = direction.len();
        float attackRange = layout.getTileSize() * FIREBALL_ATTACK_RANGE_TILES;
        if (targetDistance > attackRange) {
            return false;
        }
        if (direction.isZero(0.001f)) {
            direction.set(1f, 0f);
        } else {
            direction.nor();
        }

        float startCenterX = bossCenterX + direction.x * boss.getWidth() * 0.40f;
        float startCenterY = bossCenterY + direction.y * boss.getHeight() * 0.24f;
        float fireballSize = Math.max(layout.getTileSize() * 0.42f, 12f);
        float fireballSpeed = Math.max(FIREBALL_SPEED, layout.getTileSize() * 6.4f);

        BossFireballEntity fireball = new BossFireballEntity(
            "boss_fireball_" + fireballSequence++,
            startCenterX,
            startCenterY,
            playerCenterX,
            playerCenterY,
            fireballSize,
            fireballSpeed,
            FIREBALL_LIFETIME_SECONDS
        );
        bossFireballs.add(fireball);
        world.addCollidableEntity(fireball, createFireballListener(fireball));
        return true;
    }

    private GridPoint2 selectBombTargetCell(MazeLayout.Layout layout, PlayerEntity player) {
        Random rng = LingoSession.get().getRandom();
        GridPoint2 playerCell = player == null
            ? null
            : MazeLayout.toCell(layout, player.getX(), player.getY(), player.getWidth());
        boolean canTargetPlayer = playerCell != null && MazeLayout.isOpen(layout, playerCell.x, playerCell.y);

        if (canTargetPlayer && rng.nextFloat() < BOMB_TARGET_PLAYER_CHANCE) {
            return new GridPoint2(playerCell);
        }

        GridPoint2 nearbyPathCell = chooseNearbyBombTargetCell(layout, playerCell, rng);
        if (nearbyPathCell != null) {
            return nearbyPathCell;
        }

        GridPoint2 randomOpenCell = chooseRandomBombTargetCell(layout, playerCell, rng);
        return randomOpenCell != null ? randomOpenCell : (canTargetPlayer ? new GridPoint2(playerCell) : null);
    }

    private GridPoint2 chooseNearbyBombTargetCell(MazeLayout.Layout layout, GridPoint2 playerCell, Random rng) {
        if (playerCell == null) {
            return null;
        }

        List<GridPoint2> nearbyCells = new ArrayList<>();
        List<GridPoint2> closestCells = new ArrayList<>();
        int closestDistance = Integer.MAX_VALUE;

        for (GridPoint2 cell : bombDropCells) {
            if (cell == null || cell.equals(playerCell) || !MazeLayout.isOpen(layout, cell.x, cell.y)) {
                continue;
            }

            int distance = manhattanDistance(cell, playerCell);
            if (distance <= BOMB_NEAR_PLAYER_RADIUS) {
                nearbyCells.add(cell);
            }
            if (distance < closestDistance) {
                closestDistance = distance;
                closestCells.clear();
                closestCells.add(cell);
            } else if (distance == closestDistance) {
                closestCells.add(cell);
            }
        }

        if (!nearbyCells.isEmpty()) {
            GridPoint2 chosen = nearbyCells.get(rng.nextInt(nearbyCells.size()));
            return new GridPoint2(chosen);
        }
        if (!closestCells.isEmpty()) {
            GridPoint2 chosen = closestCells.get(rng.nextInt(closestCells.size()));
            return new GridPoint2(chosen);
        }
        return null;
    }

    private GridPoint2 chooseRandomBombTargetCell(MazeLayout.Layout layout, GridPoint2 excludedCell, Random rng) {
        List<GridPoint2> shuffled = new ArrayList<>(bombDropCells);
        Collections.shuffle(shuffled, rng);
        for (GridPoint2 cell : shuffled) {
            if (cell == null || !MazeLayout.isOpen(layout, cell.x, cell.y)) {
                continue;
            }
            if (excludedCell != null && shuffled.size() > 1 && excludedCell.equals(cell)) {
                continue;
            }
            return new GridPoint2(cell);
        }
        return null;
    }

    private int manhattanDistance(GridPoint2 a, GridPoint2 b) {
        if (a == null || b == null) {
            return Integer.MAX_VALUE;
        }
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private void cleanupInactiveBombs() {
        if (wallBombs.isEmpty()) {
            return;
        }

        List<WallBombEntity> expired = new ArrayList<>();
        for (WallBombEntity bomb : wallBombs) {
            if (bomb == null || bomb.isActive()) {
                continue;
            }

            expired.add(bomb);
            world.removeEntity(bomb);
        }

        if (!expired.isEmpty()) {
            wallBombs.removeAll(expired);
        }
    }

    private void cleanupInactiveFireballs() {
        if (bossFireballs.isEmpty()) {
            return;
        }

        List<BossFireballEntity> expired = new ArrayList<>();
        for (BossFireballEntity fireball : bossFireballs) {
            if (fireball == null || fireball.isActive()) {
                continue;
            }

            expired.add(fireball);
            world.removeEntity(fireball);
        }

        if (!expired.isEmpty()) {
            bossFireballs.removeAll(expired);
        }
    }

    private boolean hasActiveWallBomb() {
        for (WallBombEntity bomb : wallBombs) {
            if (bomb != null && bomb.isActive()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasActiveBossFireball() {
        for (BossFireballEntity fireball : bossFireballs) {
            if (fireball != null && fireball.isActive()) {
                return true;
            }
        }
        return false;
    }

    private void clearAttacks() {
        clearBombs();
        clearFireballs();
    }

    private void clearBombs() {
        if (wallBombs.isEmpty()) {
            return;
        }

        for (WallBombEntity bomb : new ArrayList<>(wallBombs)) {
            bomb.expire();
            world.removeEntity(bomb);
        }
        wallBombs.clear();
    }

    private void clearFireballs() {
        if (bossFireballs.isEmpty()) {
            return;
        }

        for (BossFireballEntity fireball : new ArrayList<>(bossFireballs)) {
            fireball.expire();
            world.removeEntity(fireball);
        }
        bossFireballs.clear();
    }

    private void resetGhostShotCooldowns() {
        ghostShotCooldowns.clear();
        int shooterIndex = 0;
        for (GhostEntity ghost : ghosts) {
            GhostLoadout loadout = ghostLoadouts.get(ghost);
            if (loadout == null || !loadout.canShoot()) {
                continue;
            }
            ghostShotCooldowns.put(ghost, initialShotCooldown(loadout, shooterIndex++));
        }
    }

    private float initialShotCooldown(GhostLoadout loadout, int shooterIndex) {
        return shotCooldown(loadout) + shooterIndex * 0.6f;
    }

    private float shotCooldown(GhostLoadout loadout) {
        return loadout == null ? Float.POSITIVE_INFINITY : loadout.shotCooldownSeconds;
    }

    private List<GhostLoadout> buildGhostLoadouts(GameState.Difficulty difficulty) {
        return switch (difficulty) {
            case MEDIUM -> List.of(
                new GhostLoadout(
                    GhostEntity.GhostType.NORMAL,
                    GhostMovePattern.SEEK,
                    new Color(1.00f, 0.35f, 0.30f, 1f),
                    1.00f,
                    1.05f,
                    ShotPattern.NONE,
                    0f
                ),
                new GhostLoadout(
                    GhostEntity.GhostType.WALL_BOMBER,
                    GhostMovePattern.STATIONARY,
                    new Color(0.65f, 0.48f, 0.78f, 1f),
                    0f,
                    1.04f,
                    ShotPattern.BOMB_DROP,
                    3.10f
                )
            );
            case HARD -> List.of(
                new GhostLoadout(
                    GhostEntity.GhostType.BOSS,
                    GhostMovePattern.SEEK,
                    new Color(0.98f, 0.24f, 0.18f, 1f),
                    1.12f,
                    1.28f,
                    ShotPattern.FIREBALL,
                    2.20f
                ),
                new GhostLoadout(
                    GhostEntity.GhostType.NORMAL,
                    GhostMovePattern.WANDER,
                    new Color(0.67f, 0.47f, 0.82f, 1f),
                    0.96f,
                    1.06f,
                    ShotPattern.NONE,
                    0f
                ),
                new GhostLoadout(
                    GhostEntity.GhostType.WALL_BOMBER,
                    GhostMovePattern.STATIONARY,
                    new Color(0.65f, 0.48f, 0.78f, 1f),
                    0f,
                    1.02f,
                    ShotPattern.BOMB_DROP,
                    3.10f
                )
            );
            default -> List.of(
                new GhostLoadout(
                    GhostEntity.GhostType.NORMAL,
                    GhostMovePattern.SEEK,
                    new Color(1.00f, 0.35f, 0.30f, 1f),
                    1.00f,
                    1.05f,
                    ShotPattern.NONE,
                    0f
                )
            );
        };
    }

    private GridPoint2 findWallBomberCell(MazeLayout.Layout layout) {
        int centerCol = layout.getCols() / 2;
        int centerRow = layout.getRows() / 2;
        GridPoint2 best = null;
        int bestScore = Integer.MAX_VALUE;

        for (int row = 1; row < layout.getRows() - 1; row++) {
            for (int col = 1; col < layout.getCols() - 1; col++) {
                if (!MazeLayout.isWall(layout, col, row)) {
                    continue;
                }
                if (!hasAdjacentOpenCell(layout, col, row)) {
                    continue;
                }

                int score = Math.abs(col - centerCol) * 2 + Math.abs(row - centerRow);
                if (score < bestScore) {
                    bestScore = score;
                    best = new GridPoint2(col, row);
                }
            }
        }

        return best == null ? new GridPoint2(centerCol, centerRow) : best;
    }

    private boolean hasAdjacentOpenCell(MazeLayout.Layout layout, int col, int row) {
        return MazeLayout.isOpen(layout, col + 1, row)
            || MazeLayout.isOpen(layout, col - 1, row)
            || MazeLayout.isOpen(layout, col, row + 1)
            || MazeLayout.isOpen(layout, col, row - 1);
    }

    private ICollisionListener createWallBombListener(WallBombEntity bomb) {
        return wallBombListenerFactory == null ? null : wallBombListenerFactory.apply(bomb);
    }

    private ICollisionListener createFireballListener(BossFireballEntity fireball) {
        return fireballListenerFactory == null ? null : fireballListenerFactory.apply(fireball);
    }
}
