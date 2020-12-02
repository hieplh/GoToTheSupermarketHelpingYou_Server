package com.smhu.system;

import com.smhu.account.Shipper;
import com.smhu.controller.OrderController;
import com.smhu.controller.ShipperController;
import com.smhu.core.CoreFunctions;
import com.smhu.dao.ShipperDAO;
import com.smhu.iface.ICore;
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

    final IShipper shipperListener;
    final ICore coreListener;

    public SystemTime() {
        shipperListener = new ShipperDAO();
        coreListener = new CoreFunctions();
    }

    @Scheduled(fixedRate = 1000)
    public void runSystemTime() {
        SYSTEM_TIME += 1000;
    }

//    @Scheduled(fixedDelay = 13 * 1000)
    public void checkOrderOutOfTimeRelease() {
        System.out.println("Check Order Out Of Time Release");

        List<String> rejectedShippers = null;
        Iterator<Order> iterator = OrderController.mapOrderIsWaitingAccept.keySet().iterator();
        while (iterator.hasNext()) {
            try {
                Order order = iterator.next();
                if (SYSTEM_TIME >= OrderController.mapOrderIsWaitingAccept.get(order)) {
                    Shipper shipper = shipperListener.getShipper(order.getShipper());

                    List<String> list = OrderController.mapOrdersShipperReject.get(order.getShipper());
                    if (list == null) {
                        list = new ArrayList<>();
                        OrderController.mapOrdersShipperReject.put(shipper.getId(), list);
                    }
                    System.out.println(order);

                    if (rejectedShippers == null) {
                        rejectedShippers = new ArrayList();
                    }
                    if (!rejectedShippers.contains(shipper.getId())) {
                        rejectedShippers.add(shipper.getId());
                    }

                    list.add(order.getId());
                    OrderController.mapOrderDeliveryForShipper.remove(shipper.getId());

                    List<String> listOrderBelongToShipper = ShipperController.mapShipperOrdersInProgress.get(order.getShipper());
                    listOrderBelongToShipper.remove(order.getId());
                    if (listOrderBelongToShipper.isEmpty()) {
                        ShipperController.mapShipperOrdersInProgress.remove(order.getShipper());
                    }

                    order.setShipper(null);
                    shipper.setNumCancel(shipper.getNumCancel() + 1);
                    shipperListener.updateNumCancel(shipper.getId(), 1);

                    coreListener.filterOrder(order);
                    iterator.remove();
                }
            } catch (ClassNotFoundException | SQLException e) {
                Logger.getLogger(SystemTime.class.getName()).log(Level.SEVERE, e.getMessage());
            }
        }

        if (rejectedShippers != null) {
            synchronized (ShipperController.mapAvailableShipper) {
                for (String id : rejectedShippers) {
                    if (ShipperController.mapInProgressShipper.containsKey(id)) {
                        shipperListener.changeStatusOfShipper(id);
                    }
                }
            }
        }
        System.out.println("");
    }
}
