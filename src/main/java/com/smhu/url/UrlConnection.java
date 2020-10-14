package com.smhu.url;

import com.smhu.order.Order;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class UrlConnection {

    private static final String API_KEY = "AIzaSyCvlIOQUZEmyNxvrwKtXACB_QqycPTnAmE";
    private static final String URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial";

    public InputStream openConnection(String[] locationShipper, Map<String[][], List<Order>> locationOrder) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(URL);
        sb.append("&origins=");

        sb.append(locationShipper[0]);
        sb.append(",");
        sb.append(locationShipper[1]);

        boolean flag = false;
        sb.append("&destinations=");
        for (String[][] destLocation : locationOrder.keySet()) {
            if (flag) {
                sb.append("|");
            } else {
                flag = true;
            }

            for (String[] location : destLocation) {
                sb.append(location[0]);
                sb.append(",");
                sb.append(location[1]);
            }
        }

        sb.append("&language=vi");
        sb.append("&mode=driving");
        sb.append("&key=");
        sb.append(API_KEY);
        return new URL(sb.toString()).openStream();
    }
}
