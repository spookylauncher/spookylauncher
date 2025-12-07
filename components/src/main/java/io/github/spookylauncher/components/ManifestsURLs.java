package io.github.spookylauncher.components;

public final class ManifestsURLs extends LauncherComponent {

    public static final String[] BASE_DATA_URLS =
    {
            "https://raw.githubusercontent.com/spookylauncher/Data/refs/heads/main"
    };

    private final String baseDataURL;

    public String getBaseDataURL() {
        return this.baseDataURL;
    }

    public ManifestsURLs(final ComponentsController components) {
        super("Manifests URLs", components);

        OptimalDataURLSolver solver = new OptimalDataURLSolver(
                BASE_DATA_URLS,
                "launcher-manifest.json"
        );

        this.baseDataURL = solver.getOptimalBaseURL();
    }

    public String getLauncherManifestURL() {
        return baseDataURL + "/launcher-manifest.json";
    }

    public String getLibrariesManifestURL() {
        return baseDataURL + "/libraries-manifest.json";
    }

    public String getJREsManifestURL() {
        return baseDataURL + "/java-manifest.json";
    }

    public String getMirrorsManifestURL() {
        return baseDataURL + "/mirrors-manifest.json";
    }

    public String getVersionsManifestURL() {
        return baseDataURL + "/versions-manifest.json";
    }
}