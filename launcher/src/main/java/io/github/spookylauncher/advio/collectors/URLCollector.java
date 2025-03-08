package io.github.spookylauncher.advio.collectors;

import java.io.IOException;
import java.net.*;
import java.io.InputStream;

public final class URLCollector extends Collector {

    private long size = -1;
    public URLCollector(String url) {
        super(url.replace(" ", "%20"));
    }

    public InputStream collectBaseInput() {
        try {
            URLConnection con = new URL(path).openConnection();

            con.setRequestProperty("User-Agent", "");
            con.setDoInput(true);
            con.connect();

            if(con instanceof HttpURLConnection) {
                int response = ((HttpURLConnection)con).getResponseCode();

                if(response >= 400) throw new RuntimeException("HTTP Error " + response);
            }

            size = con.getContentLengthLong();

            return con.getInputStream();
        } catch(Exception e) {
            throw new RuntimeException(path, e);
        }
    }

    @Override
    public long size() {
        if(size == -1) {
            try {
                collectInput().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return size;
    }
}