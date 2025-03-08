package io.github.spookylauncher.components;

import java.net.*;

public final class OptimalDataURLSolver {
    private final String[] allBaseURLs;
    private final String launcherManifestName;

    public OptimalDataURLSolver(final String[] allBaseURLs, final String launcherManifestName) {
        this.allBaseURLs = allBaseURLs;
        this.launcherManifestName = launcherManifestName;
    }

    public String getOptimalBaseURL() {
        String optimalUrl = allBaseURLs[0];
        int leastResponseTime = Integer.MAX_VALUE;

        for(String baseUrl : allBaseURLs) {
            int responseTime;

            if((responseTime = getResponseTime(baseUrl)) < leastResponseTime) {
                optimalUrl = baseUrl;
                leastResponseTime = responseTime;
            }
        }

        return optimalUrl;
    }

    private int getResponseTime(final String baseURL) {
        String launcherManifestURL = baseURL + '/' + launcherManifestName;

        try {
            long startTime = System.currentTimeMillis();

            URLConnection con = new URL(launcherManifestURL).openConnection();

            long endTime = System.currentTimeMillis();

            if(con instanceof HttpURLConnection) {
                HttpURLConnection hcon = (HttpURLConnection) con;

                if(hcon.getResponseCode() >= 400) return Integer.MAX_VALUE;
                else hcon.disconnect();
            }

            return (int) (endTime - startTime);
        } catch(Exception e) {
            return Integer.MAX_VALUE;
        }
    }
}