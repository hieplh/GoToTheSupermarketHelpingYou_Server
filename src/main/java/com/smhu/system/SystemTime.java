package com.smhu.system;

import com.smhu.controller.OrderController;
import com.smhu.controller.ShipperController;
import com.smhu.order.Order;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SystemTime {

    public static long SYSTEM_TIME = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), new Locale("vi", "vn"))
            .getTimeInMillis();

    @Scheduled(fixedRate = 1000)
    public void runSystemTime() {
        SYSTEM_TIME += 1000;
    }

    //@Scheduled(fixedRate = 10 * 1000)
    public void checkOrderOutOfTimeRelease() {
//        for (Map.Entry<Order, Long> order : OrderController.mapPreOrderRelease.entrySet()) {
//            if (SYSTEM_TIME >= order.getValue()) {
//                OrderController.mapOrderInProcess.put(order.getKey().getId(), order.getKey());
//            }
//        }
        System.out.println("");
        System.out.println("Check Order Out Of Time Release");
        Iterator<Order> iterator = OrderController.mapOrderIsWaitingAccept.keySet().iterator();
        while (iterator.hasNext()) {
            try {
                Order order = iterator.next();
                if (SYSTEM_TIME >= OrderController.mapOrderIsWaitingAccept.get(order)) {
                    iterator.remove();
                    ShipperController.mapLocationInProgressShipper.remove(order.getShipper());
                    order.setShipper(null);
                    OrderController.mapOrderIsWaitingRelease.put(order.getId(), order);
                    System.out.println(order);
                }
            } catch (Exception e) {
                Logger.getLogger(SystemTime.class.getName()).log(Level.SEVERE, e.getMessage());
            }
        }
    }
}
