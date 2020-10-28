package com.smhu.schedule;

import com.smhu.controller.OrderController;
import com.smhu.google.Firebase;
import com.smhu.iface.IOrder;
import com.smhu.response.customer.OrderCustomer;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.smhu.controller.ShipperController;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Notify {

    static Map<String, Integer> mapTimeDelivery = new HashMap<>();
    static Map<String, String> mapData = new HashMap(); //ID Order - Precondition minimum distance for shipper

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    static boolean INFINITE_PUSH_ORDER_FLAG = false;
    static boolean INFINITE_CHECK_ORDER_FLAG = false;

    IOrder orderListener = new OrderController().getOrderListener();

    final int RANGE_SMALL = 1;
    final int RANGE_MEDIUM = 2;
    final int RANGE_LARGE = 3;

    final String RADIUS_SMALL = "3km";
    final String RADIUS_MEDIUM = "5km";
    final String RADIUS_LARGE = "7km";

    final String TOPIC_SHIPEPR = "SHIPPER";

    Firebase firebase = new Firebase();

    //@Scheduled(cron = "0 0 8 ? * *")
//    public void pushOrder() throws InterruptedException {
//        INFINITE_PUSH_ORDER_FLAG = true;
//        do {
//            for (OrderCustomer order : OrderController.mapOrderInProcess.values()) {
//                if (mapData.containsKey(order.getId())) {
//                    switch (Integer.parseInt(order.getId()) + 1) {
//                        case RANGE_MEDIUM:
//                            mapData.put(order.getId(), mapData.get(RADIUS_MEDIUM));
//                            break;
//                        case RANGE_LARGE:
//                            mapData.put(order.getId(), mapData.get(RADIUS_LARGE));
//                            break;
//                        default:
//                            mapData.put(order.getId(), mapData.get(RADIUS_SMALL));
//                            break;
//                    }
//                } else {
//                    mapData.put(order.getId(), RADIUS_SMALL);
//                }
//            }
//
//            try {
//                if (!mapData.isEmpty()) {
//                    firebase.pushNotifyOrdedrToShipper(TOPIC_SHIPEPR, mapData);
//                }
//            } catch (FirebaseMessagingException | IOException ex) {
//                Logger.getLogger(Notify.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            if (LocalTime.of(20, 30, 0).compareTo(getCurrentTime()) <= -1) {
//                INFINITE_PUSH_ORDER_FLAG = false;
//                clear();
//            } else {
//                Thread.sleep(5 * 60 * 1000);
//            }
//        } while (INFINITE_PUSH_ORDER_FLAG);
//    }
    @Scheduled(fixedRate = 30 * 1000)
    public void checkOrderInQueue() {
        orderListener.checkOrderInqueue();
    }

    private void clear() {
        if (!OrderController.mapOrderInQueue.isEmpty()) {
            OrderController.mapOrderInQueue.clear();
        }

        if (!OrderController.mapOrderIsWaitingRelease.isEmpty()) {
            OrderController.mapOrderIsWaitingRelease.clear();
        }

        if (!OrderController.mapOrderIsWaitingAccept.isEmpty()) {
            OrderController.mapOrderIsWaitingAccept.clear();
        }

        if (!OrderController.mapOrderInProgress.isEmpty()) {
            OrderController.mapOrderInProgress.clear();
        }

        if (!OrderController.mapOrderIsDone.isEmpty()) {
            OrderController.mapOrderIsDone.clear();
        }

        if (!OrderController.mapOrderInCancelQueue.isEmpty()) {
            OrderController.mapOrderInCancelQueue.clear();
        }

        if (!OrderController.mapOrderIsCancel.isEmpty()) {
            OrderController.mapOrderIsCancel.clear();
        }

        if (!ShipperController.mapLocationAvailableShipper.isEmpty()) {
            ShipperController.mapLocationAvailableShipper.clear();
        }

        if (!ShipperController.mapLocationAvailableShipper.isEmpty()) {
            ShipperController.mapLocationInProgressShipper.clear();
        }
    }
}
