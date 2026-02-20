package io.github.some_example_name;

/**
 * Configures and extends engine runtime behaviour from an external module.
 */
@FunctionalInterface
public interface EngineBootstrap {

    EngineBootstrap NO_OP = context -> {
    };

    void initialize(EngineContext context);

    default void update(EngineContext context, float deltaTime) {
    }

    default void dispose(EngineContext context) {
    }
}
