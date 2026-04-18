package io.github.some_example_name.teavm;

import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompiler;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import org.teavm.vm.TeaVMOptimizationLevel;

/**
 * Builds the browser bundle for Lingo-Man.
 */
public final class BuildLingoManWeb {

    private static final String ASSETS_PATH = "../assets";
    private static final String LOADING_MARKUP = """
        <div id="loading-overlay">
            <div id="loading-panel">
                <img class="loading-art" src="assets/lingoman/startmenu.png" alt="Lingo-Man start screen" />
                <div class="loading-chip">Preparing game...</div>
            </div>
        </div>
        <div>
            <canvas id="canvas"></canvas>
        </div>
        """;

    private static final String LOADING_STYLES = """
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            background: #081427;
            height: 100vh;
            margin: 0;
            padding: 0;
            overflow: hidden;
            font-family: Arial, sans-serif;
        }
        #canvas {
            border: none;
        }
        #loading-overlay {
            position: fixed;
            inset: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #081427;
            z-index: 10;
            transition: opacity 0.25s ease;
        }
        #loading-overlay.ready {
            opacity: 0;
            pointer-events: none;
        }
        #loading-panel {
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
            width: min(960px, 92vw);
            max-height: 88vh;
        }
        .loading-art {
            display: block;
            width: 100%;
            height: auto;
            max-height: 88vh;
            object-fit: contain;
            image-rendering: auto;
        }
        .loading-chip {
            position: absolute;
            left: 50%;
            bottom: 4.5%;
            transform: translateX(-50%);
            padding: 10px 20px;
            border: 2px solid rgba(111, 235, 255, 0.9);
            background: rgba(8, 35, 78, 0.92);
            color: #d7f6ff;
            font-size: clamp(16px, 2vw, 24px);
            letter-spacing: 0.6px;
            box-shadow: 0 0 16px rgba(86, 214, 255, 0.18);
        }
        """;

    private static final String INDEX_HTML = """
        <!DOCTYPE html>
        <html>
            <head>
                <title>Lingo-Man</title>
                <meta http-equiv="Content-Type" content="text/html;charset=utf-8">
                <style>
        %s
                </style>
            </head>
            <body oncontextmenu="return false">
        %s
                <script>
                    async function start() {
                        main()
                    }
                    window.addEventListener("load", start);
                </script>
                <script type="text/javascript" charset="utf-8" src="app.js"></script>
            </body>
        </html>
        """;

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

        File outputDir = new File("build/dist");

        new TeaCompiler(backend)
            .addAssets(new AssetFileHandle(ASSETS_PATH))
            .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
            .setMainClass(LingoManWebLauncher.class.getName())
            .setObfuscated(false)
            .build(outputDir);

        customizeGeneratedIndex(outputDir);
    }

    private static void customizeGeneratedIndex(File outputDir) {
        File indexFile = new File(outputDir, "webapp/index.html");
        if (!indexFile.isFile()) {
            return;
        }

        try {
            String html = INDEX_HTML.formatted(LOADING_STYLES, LOADING_MARKUP);
            Files.writeString(indexFile.toPath(), html, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to customize generated web index", exception);
        }
    }
}
