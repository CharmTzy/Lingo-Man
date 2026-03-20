package io.github.some_example_name.lingoman.model;

import java.util.List;
import java.util.Map;
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

    private static final Map<String, String> WORD_MEANINGS = Map.ofEntries(
        Map.entry("CAT", "a small domesticated feline mammal"),
        Map.entry("DOG", "a domesticated canine often kept as a pet"),
        Map.entry("SUN", "the star at the center of our solar system"),
        Map.entry("MOON", "a natural satellite that orbits a planet"),
        Map.entry("STAR", "a luminous sphere of hot gas in space"),
        Map.entry("TREE", "a tall plant with a trunk and branches"),
        Map.entry("BIRD", "a feathered animal, usually able to fly"),
        Map.entry("FISH", "a cold-blooded animal that lives in water"),
        Map.entry("ORANGE", "a citrus fruit with a sweet-tart taste"),
        Map.entry("PLANET", "a large body that orbits a star"),
        Map.entry("PENCIL", "a writing tool with graphite core"),
        Map.entry("FLOWER", "the blooming part of a plant"),
        Map.entry("ROCKET", "a vehicle propelled by thrust"),
        Map.entry("WINDOW", "an opening in a wall that lets in light"),
        Map.entry("LANGUAGE", "a system used by people to communicate"),
        Map.entry("ECLIPSES", "events where one celestial body blocks another"),
        Map.entry("ELEPHANT", "a very large mammal with a trunk"),
        Map.entry("MAGNETIC", "having properties related to magnetism"),
        Map.entry("NOTEBOOK", "a book of blank pages for writing notes")
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

    public static String meaningFor(String word) {
        if (word == null || word.isBlank()) {
            return "";
        }
        return WORD_MEANINGS.getOrDefault(word.trim().toUpperCase(), "meaning unavailable");
    }
}