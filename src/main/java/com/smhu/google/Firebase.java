package com.smhu.google;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

public class Firebase {

    @Value("${app.firebase-configuration-fil}")
    private String pathFirebaseConfig;

    private void initOptions() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(
                        new ClassPathResource(pathFirebaseConfig)
                                .getInputStream()))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

    public BatchResponse pushNotifyOrdedrToShipper(String topic, Map<String, String> mapData) throws FirebaseMessagingException, IOException {
        initOptions();
        List<Message> list = null;
        for (Map.Entry<String, String> data : mapData.entrySet()) {
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(Message.builder()
                    .setTopic(topic)
                    .putData("order", data.getKey())
                    .putData("distance", data.getValue())
                    .build());
        }

        return FirebaseMessaging.getInstance().sendAll(list);
    }

    public String pushNotifyLocationOfShipperToCustomer(String topic, String lat, String lng) throws FirebaseMessagingException, IOException {
        initOptions();
        return FirebaseMessaging.getInstance().send(Message.builder()
                .setTopic(topic)
                .putData("lat", lat)
                .putData("lng", lng)
                .build());
    }
}
