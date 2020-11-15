package com.smhu.system;

import com.smhu.account.Shipper;
import com.smhu.controller.OrderController;
import com.smhu.controller.ShipperController;
import com.smhu.iface.IShipper;
import com.smhu.order.Order;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
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
        System.out.println("");
        System.out.println("Check Order Out Of Time Release");
        
        IShipper shipperListener = new ShipperController().getShipperListener();
        List<String> listShipperId = null;
        Iterator<Order> iterator = OrderController.mapOrderIsWaitingAccept.keySet().iterator();
        while (iterator.hasNext()) {
            try {
                Order order = iterator.next();
                if (SYSTEM_TIME >= OrderController.mapOrderIsWaitingAccept.get(order)) {
                    Shipper shipper = shipperListener.getShipper(order.getShipper());
                    if (listShipperId == null) {
                        listShipperId = new ArrayList<>();
                    }
                    if (!listShipperId.contains(order.getShipper())) {
                        listShipperId.add(order.getShipper());
                    }
                    
                    List<String> list = OrderController.mapOrdersShipperReject.get(order.getShipper());
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    System.out.println(order);
                    
                    list.add(order.getId());
                    OrderController.mapOrdersShipperReject.put(shipper.getId(), list);
                    OrderController.mapOrderDeliveryForShipper.remove(shipper.getId());
                    order.setShipper(null);
                    OrderController.mapOrderInQueue.put(order.getId(), order);
                    
                    shipper.setNumCancel(shipper.getNumCancel() + 1);
                    shipperListener.updateNumCancelOfShipper(shipper.getId(), 1);
                    
                    iterator.remove();
                }
            } catch (ClassNotFoundException | SQLException e) {
                Logger.getLogger(SystemTime.class.getName()).log(Level.SEVERE, e.getMessage());
            }
        }
        
        if (listShipperId != null) {
            synchronized (ShipperController.mapAvailableShipper) {
                for (String id : listShipperId) {
                    shipperListener.changeStatusOfShipper(id);
                }
            }
        }
    }
}
