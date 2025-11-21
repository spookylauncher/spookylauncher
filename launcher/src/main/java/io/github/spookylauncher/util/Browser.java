package io.github.spookylauncher.util;

public final class Browser {
    public static boolean openURL(String url) {
        try {
            String cmd = null;
            String os = System.getProperty("os.name").toLowerCase();
            boolean linux = os.contains("linux");

            if(os.contains("win")) {
                cmd = "rundll32 url.dll,FileProtocolHandler";
            } else if(os.contains("mac")) {
                cmd = "open";
            } else if(!linux) return false;

            if(!linux) Runtime.getRuntime().exec(cmd + " " + url);
            else {
                String[] browsers = { "google-chrome", "firefox", "mozilla", "epiphany", "konqueror",
                        "netscape", "opera", "links", "lynx" };

                StringBuffer cmdBuilder = new StringBuffer();
                for (int i = 0; i < browsers.length; i++)
                    if(i == 0)
                        cmdBuilder.append(String.format(    "%s \"%s\"", browsers[i], url));
                    else
                        cmdBuilder.append(String.format(" || %s \"%s\"", browsers[i], url));

                Runtime.getRuntime().exec(new String[] { "sh", "-c", cmdBuilder.toString() });
            }

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}