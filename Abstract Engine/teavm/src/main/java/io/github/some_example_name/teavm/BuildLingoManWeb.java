package io.github.some_example_name.teavm;

import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompiler;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import java.io.File;
import java.util.Arrays;
import org.teavm.vm.TeaVMOptimizationLevel;

/**
 * Builds the browser bundle for Lingo-Man.
 */
public final class BuildLingoManWeb {

    private static final String ASSETS_PATH = "../assets";

    private BuildLingoManWeb() {
    }

    public static void main(String[] args) {
        boolean serveAfterBuild = Arrays.asList(args).contains("--serve");

        WebBackend backend = new WebBackend()
            .setHtmlTitle("Lingo-Man")
            .setHtmlWidth(1280)
            .setHtmlHeight(720)
            .setStartJettyAfterBuild(serveAfterBuild)
            .setJettyPort(8080);

        new TeaCompiler(backend)
            .addAssets(new AssetFileHandle(ASSETS_PATH))
            .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
            .setMainClass(LingoManWebLauncher.class.getName())
            .setObfuscated(false)
            .build(new File("build/dist"));
    }
}
