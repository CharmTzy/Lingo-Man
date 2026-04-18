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
                <div class="loading-title">Lingo-Man</div>
                <div class="loading-subtitle">Loading web game...</div>
                <div class="loading-bar">
                    <div class="loading-bar-fill"></div>
                </div>
                <div class="loading-hint">The first load can take a few seconds while assets are prepared.</div>
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
            background: radial-gradient(circle at center, #123a74 0%, #071733 58%, #020813 100%);
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
            background: rgba(2, 8, 19, 0.82);
            z-index: 10;
            transition: opacity 0.25s ease;
        }
        #loading-overlay.ready {
            opacity: 0;
            pointer-events: none;
        }
        #loading-panel {
            width: min(460px, 86vw);
            padding: 28px 30px;
            border: 3px solid rgba(103, 234, 255, 0.92);
            background: rgba(9, 26, 58, 0.94);
            box-shadow: 0 0 0 8px rgba(13, 48, 92, 0.34);
            text-align: center;
            color: #e8fbff;
        }
        .loading-title {
            font-size: 42px;
            line-height: 1;
            font-weight: 700;
            letter-spacing: 1px;
            color: #ffbf40;
            text-shadow: 0 0 14px rgba(255, 167, 44, 0.28);
        }
        .loading-subtitle {
            margin-top: 12px;
            font-size: 20px;
            color: #8fe9ff;
        }
        .loading-bar {
            margin-top: 22px;
            height: 14px;
            overflow: hidden;
            border: 2px solid rgba(103, 234, 255, 0.7);
            background: rgba(16, 48, 89, 0.92);
        }
        .loading-bar-fill {
            width: 40%;
            height: 100%;
            background: linear-gradient(90deg, #ffb533 0%, #67eaff 100%);
            animation: loading-slide 1.15s ease-in-out infinite alternate;
        }
        .loading-hint {
            margin-top: 16px;
            font-size: 14px;
            line-height: 1.45;
            color: rgba(230, 245, 255, 0.8);
        }
        @keyframes loading-slide {
            from { transform: translateX(-55%); }
            to { transform: translateX(150%); }
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
