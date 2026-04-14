package io.github.some_example_name.teavm;

import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import io.github.some_example_name.Main;
import io.github.some_example_name.lingoman.LingoBootstrap;

/**
 * Launches Lingo-Man in the browser via TeaVM.
 */
public final class LingoManWebLauncher {

    private LingoManWebLauncher() {
    }

    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        config.storagePrefix = "lingoman";
        config.localStoragePrefix = "lingoman-db/assets";
        config.usePhysicalPixels = true;

        new WebApplication(new Main(new LingoBootstrap()), config);
    }
}
