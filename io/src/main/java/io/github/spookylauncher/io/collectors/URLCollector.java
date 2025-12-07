package io.github.spookylauncher.io.collectors;

import java.io.IOException;
import java.net.*;
import java.io.InputStream;

public final class URLCollector extends Collector {

    private long size = -1;

    public URLCollector(String url) throws URISyntaxException {
        super(new URI(url).toASCIIString());
    }

    public InputStream collectBaseInput() throws IOException {
        URLConnection con = new URL(path).openConnection();

        con.setRequestProperty("User-Agent", "");
        con.setDoInput(true);
        con.connect();

        if(con instanceof HttpURLConnection) {
            int response = ((HttpURLConnection)con).getResponseCode();

            if(response >= 400) throw new IOException("HTTP Error " + response);
        }

        size = con.getContentLengthLong();

        return con.getInputStream();
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