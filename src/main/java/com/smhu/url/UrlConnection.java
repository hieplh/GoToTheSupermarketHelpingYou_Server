package com.smhu.url;

import com.smhu.helper.PropertiesWithJavaConfig;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

public class UrlConnection {

    private static final String URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial";

    public UrlConnection() {
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    public InputStream openConnectionFromSourceToDestination(String[] oldShipper, String[] newShipper) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(URL);
        sb.append("&origins=");

        sb.append(oldShipper[0]);
        sb.append(",");
        sb.append(oldShipper[1]);

        sb.append("&destinations=");
        sb.append(newShipper[0]);
        sb.append(",");
        sb.append(newShipper[1]);

        sb.append("&language=vi");
        sb.append("&mode=driving");
        sb.append("&key=");
        sb.append(PropertiesWithJavaConfig.PROPERTIES.get("google.api.key.distance.matrix"));

        return new URL(sb.toString()).openStream();
    }

    public InputStream openConnectionFromSourceToDestination(String[] source, Set<String[]> destination) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(URL);
        sb.append("&origins=");

        sb.append(source[0]);
        sb.append(",");
        sb.append(source[1]);

        boolean flag = false;
        sb.append("&destinations=");
        for (String[] location : destination) {
            if (flag) {
                sb.append("|");
            } else {
                flag = true;
            }
            sb.append(location[0]);
            sb.append(",");
            sb.append(location[1]);
        }

        sb.append("&language=vi");
        sb.append("&mode=driving");
        sb.append("&key=");
        sb.append(PropertiesWithJavaConfig.PROPERTIES.get("google.api.key.distance.matrix"));

        return new URL(sb.toString()).openStream();
    }

    public InputStream openConnectionFromSourceToDestination(String[] source, List<String> destination) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(URL);
        sb.append("&origins=");

        sb.append(source[0]);
        sb.append(",");
        sb.append(source[1]);

        boolean flag = false;
        sb.append("&destinations=");
        for (String location : destination) {
            if (flag) {
                sb.append("|");
            } else {
                flag = true;
            }
            sb.append(URLEncoder.encode(location, "utf-8"));
        }

        sb.append("&language=vi");
        sb.append("&mode=driving");
        sb.append("&key=");
        sb.append(PropertiesWithJavaConfig.PROPERTIES.get("google.api.key.distance.matrix"));

        return new URL(sb.toString()).openStream();
    }
}
