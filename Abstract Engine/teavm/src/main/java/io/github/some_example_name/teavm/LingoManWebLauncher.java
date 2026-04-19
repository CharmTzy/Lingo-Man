package io.github.some_example_name.teavm;

import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import io.github.some_example_name.BrowserBridge;
import io.github.some_example_name.Main;
import io.github.some_example_name.lingoman.LingoBootstrap;
import org.teavm.jso.JSBody;
import org.teavm.jso.browser.Window;

/**
 * Launches Lingo-Man in the browser via TeaVM.
 */
public final class LingoManWebLauncher {

    private static final String WEB_STORAGE_VERSION = "lingoman-20260417";
    private static final String WEB_ASSET_STORAGE_VERSION = "lingoman-db-20260417/assets";

    private LingoManWebLauncher() {
    }

    public static void main(String[] args) {
        BrowserBridge.setBrowserEnvironment(true);
        BrowserBridge.setExitHandler(() -> {
            Window window = Window.current();
            if (window == null) {
                return false;
            }
            return attemptBrowserExit();
        });

        BrowserBridge.setReadyHandler(LingoManWebLauncher::hideLoadingOverlay);

        WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        config.storagePrefix = WEB_STORAGE_VERSION;
        config.localStoragePrefix = WEB_ASSET_STORAGE_VERSION;
        config.usePhysicalPixels = true;

        new WebApplication(new Main(new LingoBootstrap()), config);
    }

    @JSBody(script =
        "var overlay = document.getElementById('loading-overlay');" +
        "if (!overlay) return;" +
        "overlay.classList.add('ready');" +
        "setTimeout(function() {" +
        "  if (overlay && overlay.parentNode) overlay.parentNode.removeChild(overlay);" +
        "}, 250);")
    private static native void hideLoadingOverlay();

    @JSBody(script =
        "window.__lingoQuitInProgress = true;" +
        "window.addEventListener('error', function (event) {" +
        "  var message = String((event && event.message) || '');" +
        "  if (window.__lingoQuitInProgress && message.indexOf(\"$pause\") !== -1) {" +
        "    event.preventDefault();" +
        "    return false;" +
        "  }" +
        "}, { once: true });" +
        "try { window.close(); } catch (e) {}" +
        "return true;")
    private static native boolean attemptBrowserExit();
}
