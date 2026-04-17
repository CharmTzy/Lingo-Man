package io.github.some_example_name;

/**
 * Optional browser-only hooks that can be installed by a web launcher while
 * keeping the core/game code platform-agnostic.
 */
public final class BrowserBridge {

    @FunctionalInterface
    public interface ExitHandler {
        boolean requestExit();
    }

    private static ExitHandler exitHandler;

    private BrowserBridge() {
    }

    public static void setExitHandler(ExitHandler handler) {
        exitHandler = handler;
    }

    public static boolean requestExit() {
        return exitHandler != null && exitHandler.requestExit();
    }
}
