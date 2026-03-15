package io.github.some_example_name.lingoman.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lingoman.level.MazeLayout;
import io.github.some_example_name.movement.Movable;

public final class MazeWanderBehaviour extends AbstractMazeBehaviour {

    private final Random rng = new Random();
    private final List<GridPoint2> openCells;
    private GridPoint2 currentDestination;
    private GridPoint2 currentStep;

    public MazeWanderBehaviour(MazeLayout.Layout layout, float speed) {
        super(layout, speed);
        this.openCells = new ArrayList<>(MazeLayout.collectReachableOpenCells(layout, layout.getPlayerSpawn()));
    }

    @Override
    public void move(Movable entity, float deltaTime) {
        GridPoint2 current = currentCell(entity);
        if (currentDestination == null || current.equals(currentDestination)
            || isNearCell(entity, currentDestination, Math.max(2f, layout.getTileSize() * 0.12f))) {
            currentDestination = chooseNextDestination(current);
            currentStep = null;
        }

        if (currentDestination == null) {
            entity.setVelocity(new Vector2());
            return;
        }

        if (currentStep == null || isNearCell(entity, currentStep, Math.max(2f, layout.getTileSize() * 0.16f))) {
            currentStep = nextStepTowards(entity, currentDestination);
        }

        if (currentStep == null) {
            currentDestination = chooseNextDestination(current);
            currentStep = currentDestination == null ? null : nextStepTowards(entity, currentDestination);
        }

        moveToCell(entity, currentStep == null ? currentDestination : currentStep);
    }

    private GridPoint2 chooseNextDestination(GridPoint2 current) {
        if (openCells.isEmpty()) {
            return null;
        }

        List<GridPoint2> candidates = new ArrayList<>();
        List<GridPoint2> farCells = new ArrayList<>();
        for (GridPoint2 cell : openCells) {
            if (cell.equals(current)) {
                continue;
            }

            candidates.add(cell);
            int distance = Math.abs(cell.x - current.x) + Math.abs(cell.y - current.y);
            if (distance >= 4) {
                farCells.add(cell);
            }
        }

        List<GridPoint2> pool = farCells.isEmpty() ? candidates : farCells;
        if (pool.isEmpty()) {
            return null;
        }

        GridPoint2 chosen = pool.get(rng.nextInt(pool.size()));
        return new GridPoint2(chosen);
    }
}
