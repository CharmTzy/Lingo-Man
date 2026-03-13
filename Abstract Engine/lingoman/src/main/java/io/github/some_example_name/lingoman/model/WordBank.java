package io.github.some_example_name.lingoman.model;

import java.util.List;
import java.util.Random;

public final class WordBank {

    private static final List<String> EASY_WORDS = List.of(
        "CAT", "DOG", "SUN", "MOON", "STAR", "TREE", "BIRD", "FISH"
    );

    private static final List<String> MEDIUM_WORDS = List.of(
        "ORANGE", "PLANET", "PENCIL", "FLOWER", "ROCKET", "WINDOW"
    );

    private static final List<String> HARD_WORDS = List.of(
        "LANGUAGE", "ECLIPSES", "ELEPHANT", "MAGNETIC", "NOTEBOOK"
    );

    private WordBank() {
    }

    public static String randomWord(GameState.Difficulty difficulty, Random rng) {
        if (rng == null) {
            rng = new Random();
        }
        List<String> bucket = switch (difficulty) {
            case MEDIUM -> MEDIUM_WORDS;
            case HARD -> HARD_WORDS;
            default -> EASY_WORDS;
        };
        return bucket.get(rng.nextInt(bucket.size()));
    }
}