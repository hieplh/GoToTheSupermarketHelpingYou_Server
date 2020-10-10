package com.capstone.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UrlConnection {

    private static final String API_KEY = "AIzaSyCvlIOQUZEmyNxvrwKtXACB_QqycPTnAmE";
    private static final String URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial";

    public InputStream openConnection(String[] locationShipper, String[]... locationOrder) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(URL);
        sb.append("&origins=");

        sb.append(locationShipper[0]);
        sb.append(",");
        sb.append(locationShipper[1]);

        boolean flag = false;
        sb.append("&destinations=");
        for (String[] order : locationOrder) {
            if (flag) {
                sb.append("|");
            } else {
                flag = true;
            }
            
            sb.append(order[0]);
            sb.append(",");
            sb.append(order[1]);
        }
        
        sb.append("&language=vi");
        sb.append("&mode=driving");
        sb.append("&key=");
        sb.append(API_KEY);
        return new URL(sb.toString()).openStream();
    }
}
