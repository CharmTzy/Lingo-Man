package io.github.some_example_name.lingoman.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

public final class MazeLayout {

    public static final int TILE_SIZE = 32;
    public static final int COLS = 20;
    public static final int ROWS = 15;

    private static final String[] MAP = {
        "####################",
        "#........#.........#",
        "#.####...#..####...#",
        "#.#..#........#....#",
        "#.#..####..####..#.#",
        "#.#..............#.#",
        "#.####..####..####.#",
        "#......#....#......#",
        "#.####.#.##.#.####.#",
        "#....#.#....#.#....#",
        "####.#.######.#.####",
        "#........#.........#",
        "#.####...#..####...#",
        "#........#.........#",
        "####################"
    };

    private MazeLayout() {
    }

    public static boolean isWall(int col, int row) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return true;
        }
        return MAP[row].charAt(col) == '#';
    }

    public static List<GridPoint2> collectOpenCells() {
        List<GridPoint2> open = new ArrayList<>();
        for (int row = 0; row < ROWS; row++) {
            String line = MAP[row];
            for (int col = 0; col < COLS; col++) {
                if (line.charAt(col) != '#') {
                    open.add(new GridPoint2(col, row));
                }
            }
        }
        return open;
    }

    public static float toWorldX(int col) {
        return col * TILE_SIZE;
    }

    public static float toWorldY(int row) {
        return (ROWS - 1 - row) * TILE_SIZE;
    }

    public static float toWorldXCentered(int col, float size) {
        return toWorldX(col) + (TILE_SIZE - size) * 0.5f;
    }

    public static float toWorldYCentered(int row, float size) {
        return toWorldY(row) + (TILE_SIZE - size) * 0.5f;
    }
}
