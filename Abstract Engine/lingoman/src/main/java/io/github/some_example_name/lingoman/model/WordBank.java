package io.github.some_example_name.lingoman.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class WordBank {

    private static final List<String> EASY_WORDS = Collections.unmodifiableList(Arrays.asList(
        "CAT", "DOG", "SUN", "MOON", "STAR", "TREE", "BIRD", "FISH"
    ));

    private static final List<String> MEDIUM_WORDS = Collections.unmodifiableList(Arrays.asList(
        "ORANGE", "PLANET", "PENCIL", "FLOWER", "ROCKET", "WINDOW"
    ));

    private static final List<String> HARD_WORDS = Collections.unmodifiableList(Arrays.asList(
        "LANGUAGE", "ECLIPSES", "ELEPHANT", "MAGNETIC", "NOTEBOOK"
    ));

    private static final Map<String, String> WORD_MEANINGS = createWordMeanings();

    private WordBank() {
    }

    private static Map<String, String> createWordMeanings() {
        Map<String, String> meanings = new LinkedHashMap<>();
        meanings.put("CAT", "a small domesticated feline mammal");
        meanings.put("DOG", "a domesticated canine often kept as a pet");
        meanings.put("SUN", "the star at the center of our solar system");
        meanings.put("MOON", "a natural satellite that orbits a planet");
        meanings.put("STAR", "a luminous sphere of hot gas in space");
        meanings.put("TREE", "a tall plant with a trunk and branches");
        meanings.put("BIRD", "a feathered animal, usually able to fly");
        meanings.put("FISH", "a cold-blooded animal that lives in water");
        meanings.put("ORANGE", "a citrus fruit with a sweet-tart taste");
        meanings.put("PLANET", "a large body that orbits a star");
        meanings.put("PENCIL", "a writing tool with graphite core");
        meanings.put("FLOWER", "the blooming part of a plant");
        meanings.put("ROCKET", "a vehicle propelled by thrust");
        meanings.put("WINDOW", "an opening in a wall that lets in light");
        meanings.put("LANGUAGE", "a system used by people to communicate");
        meanings.put("ECLIPSES", "events where one celestial body blocks another");
        meanings.put("ELEPHANT", "a very large mammal with a trunk");
        meanings.put("MAGNETIC", "having properties related to magnetism");
        meanings.put("NOTEBOOK", "a book of blank pages for writing notes");
        return Collections.unmodifiableMap(meanings);
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
