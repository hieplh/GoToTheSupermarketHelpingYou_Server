package com.smhu.google;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.io.IOException;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;

public class Firebase {

    String pathFirebaseConfig = "gothetosupermarkethelpingyou-firebase-adminsdk-hxbba-c08e13d162.json";

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

    public String pushNotifyOrdersToShipper(String token, Map<String, String> map) throws FirebaseMessagingException, IOException {
        initOptions();
        return FirebaseMessaging.getInstance().send(Message.builder()
                .setToken(token)
                .putAllData(map)
                .build());
    }
    
    public String pushNotifyOrderIsCancelByStaffForShipper(String token, Map<String, String> map) throws FirebaseMessagingException, IOException {
        initOptions();
        return FirebaseMessaging.getInstance().send(Message.builder()
                .setToken(token)
                .putAllData(map)
                .build());
    }

//    public static void main(String[] args) throws IOException, FirebaseMessagingException {
//        InputStream is = new FileInputStream("C:\\Users\\Admin\\Desktop\\Capstone\\GototheSupermarketHelpingYou\\src\\main\\resources\\gothetosupermarkethelpingyou-firebase-adminsdk-hxbba-c08e13d162.json");
//        FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(is))
//                .build();
//        if (FirebaseApp.getApps().isEmpty()) {
//            FirebaseApp.initializeApp(options);
//        }
//
//        String result = FirebaseMessaging.getInstance().send(Message.builder()
//                .setToken("ZByWMHkKE:APA91bGDxAPmcLplLITn1tVpgpvR7n0KEjhC-wucCDHlutq41c3BWM6vkBOfxFPGXeeM-pHJRrmxKTMs10suJkeZ_KZ2mvUgIGq8wKxUxQ5Nk5_r3bACKQAgY__vr03NKujV1u-VPF4i")
//                .putData("orderId", "123456789")
//                .build());
//        System.out.println("Result: " + result);
//    }
}
