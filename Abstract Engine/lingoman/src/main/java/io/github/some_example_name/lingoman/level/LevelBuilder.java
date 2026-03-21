package io.github.some_example_name.lingoman.level;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.GridPoint2;

import io.github.some_example_name.collision.ICollisionListener;
import io.github.some_example_name.lingoman.entity.FreezePickupEntity;
import io.github.some_example_name.lingoman.entity.LetterEntity;
import io.github.some_example_name.lingoman.entity.PlayerEntity;
import io.github.some_example_name.lingoman.entity.ShockPickupEntity;
import io.github.some_example_name.lingoman.entity.WallEntity;
import io.github.some_example_name.lingoman.world.LingoWorld;
import io.github.some_example_name.managers.InputManager;

public final class LevelBuilder {

    private final LingoWorld world;
    private final MazeLayout.Layout layout;
    private final Random random;

    public LevelBuilder(LingoWorld world, MazeLayout.Layout layout, Random random) {
        if (world == null) {
            throw new IllegalArgumentException("world cannot be null");
        }
        if (layout == null) {
            throw new IllegalArgumentException("layout cannot be null");
        }
        this.world = world;
        this.layout = layout;
        this.random = random == null ? new Random() : random;
    }

    public void buildWalls() {
        int id = 0;
        for (int row = 0; row < layout.getRows(); row++) {
            for (int col = 0; col < layout.getCols(); col++) {
                if (!MazeLayout.isWall(layout, col, row)) {
                    continue;
                }
                float x = MazeLayout.toWorldX(layout, col);
                float y = MazeLayout.toWorldY(layout, row);
                WallEntity wall = new WallEntity("wall_" + id++, x, y, layout.getTileSize());
                world.addCollidableEntity(wall, null);
            }
        }
    }

    public PlayerEntity buildPlayer(InputManager input, float speed, float size, ICollisionListener listener) {
        GridPoint2 spawn = layout.getPlayerSpawn();
        float x = MazeLayout.toWorldXCentered(layout, spawn.x, size);
        float y = MazeLayout.toWorldYCentered(layout, spawn.y, size);
        PlayerEntity player = new PlayerEntity("player", input, x, y, speed, size);
        world.addCollidableEntity(player, listener);
        return player;
    }

    public List<GridPoint2> collectItemCells() {
        List<GridPoint2> openCells = MazeLayout.collectReachableOpenCells(layout, layout.getPlayerSpawn());
        openCells.removeIf(this::isSpawnCell);
        Collections.shuffle(openCells, random);
        return openCells;
    }

    public List<GridPoint2> collectBombDropCells() {
        List<GridPoint2> openCells = MazeLayout.collectReachableOpenCells(layout, layout.getPlayerSpawn());
        openCells.removeIf(this::isSpawnCell);
        return openCells;
    }

    public void buildLetters(String targetWord, List<GridPoint2> openCells, float size) {
        if (targetWord == null || targetWord.isBlank() || openCells == null) {
            return;
        }

        for (int i = 0; i < targetWord.length(); i++) {
            if (openCells.isEmpty()) {
                break;
            }
            char letter = targetWord.charAt(i);
            GridPoint2 cell = openCells.remove(0);
            float x = MazeLayout.toWorldXCentered(layout, cell.x, size);
            float y = MazeLayout.toWorldYCentered(layout, cell.y, size);
            LetterEntity entity = new LetterEntity("letter_" + i, letter, x, y, size);
            world.addCollidableEntity(entity, null);
        }
    }

    public FreezePickupEntity buildFreezePickup(List<GridPoint2> openCells, float size) {
        if (openCells == null || openCells.isEmpty()) {
            return null;
        }
        GridPoint2 cell = openCells.remove(0);
        float x = MazeLayout.toWorldXCentered(layout, cell.x, size);
        float y = MazeLayout.toWorldYCentered(layout, cell.y, size);
        FreezePickupEntity entity = new FreezePickupEntity("freeze_pickup", x, y, size);
        world.addCollidableEntity(entity, null);
        return entity;
    }

    public ShockPickupEntity buildShockPickup(List<GridPoint2> openCells, float size) {
        if (openCells == null || openCells.isEmpty()) {
            return null;
        }
        GridPoint2 cell = openCells.remove(0);
        float x = MazeLayout.toWorldXCentered(layout, cell.x, size);
        float y = MazeLayout.toWorldYCentered(layout, cell.y, size);
        ShockPickupEntity entity = new ShockPickupEntity("shock_pickup", x, y, size);
        world.addCollidableEntity(entity, null);
        return entity;
    }

    private boolean isSpawnCell(GridPoint2 cell) {
        if (cell == null) {
            return false;
        }
        if (cell.equals(layout.getPlayerSpawn())) {
            return true;
        }
        for (GridPoint2 spawn : layout.getGhostSpawns()) {
            if (cell.equals(spawn)) {
                return true;
            }
        }
        return false;
    }
}