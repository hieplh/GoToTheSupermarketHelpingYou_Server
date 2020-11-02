package com.smhu.url;

import com.smhu.helper.PropertiesWithJavaConfig;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.core.io.ClassPathResource;

public class UrlConnection {

    private static final String URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial";
    private final String PATH = "application.properties";

    private Properties properties;

    public UrlConnection() {
        try {
            properties = new PropertiesWithJavaConfig(PATH).getProperties();
        } catch (IOException e) {
            try {
                properties = new Properties();
                properties.load(new ClassPathResource(PATH).getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(UrlConnection.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
    }

//    public InputStream openConnectionFromSourceToDestination(String[] locationShipper, Map<String[][], List<Order>> locationOrder) throws IOException {
//        StringBuilder sb = new StringBuilder();
//        sb.append(URL);
//        sb.append("&origins=");
//
//        sb.append(locationShipper[0]);
//        sb.append(",");
//        sb.append(locationShipper[1]);
//
//        boolean flag = false;
//        sb.append("&destinations=");
//        for (String[][] destLocation : locationOrder.keySet()) {
//            if (flag) {
//                sb.append("|");
//            } else {
//                flag = true;
//            }
//
//            for (String[] location : destLocation) {
//                sb.append(location[0]);
//                sb.append(",");
//                sb.append(location[1]);
//            }
//        }
//
//        sb.append("&language=vi");
//        sb.append("&mode=driving");
//        sb.append("&key=");
//        sb.append(properties.getProperty("google.api.key.distance.matrix"));
//        return new URL(sb.toString()).openStream();
//    }
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
        sb.append(properties.getProperty("google.api.key.distance.matrix"));
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
        sb.append(properties.getProperty("google.api.key.distance.matrix"));
        return new URL(sb.toString()).openStream();
    }
}
