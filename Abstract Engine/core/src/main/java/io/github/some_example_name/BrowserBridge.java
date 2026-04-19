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

    @FunctionalInterface
    public interface ReadyHandler {
        void onReady();
    }

    private static ExitHandler exitHandler;
    private static ReadyHandler readyHandler;
    private static boolean browserEnvironment;

    private BrowserBridge() {
    }

    public static void setExitHandler(ExitHandler handler) {
        exitHandler = handler;
    }

    public static boolean requestExit() {
        return exitHandler != null && exitHandler.requestExit();
    }

    public static void setReadyHandler(ReadyHandler handler) {
        readyHandler = handler;
    }

    public static void signalReady() {
        if (readyHandler != null) {
            readyHandler.onReady();
        }
    }

    public static void setBrowserEnvironment(boolean browserEnvironment) {
        BrowserBridge.browserEnvironment = browserEnvironment;
    }

    public static boolean isBrowserEnvironment() {
        return browserEnvironment;
    }
}
