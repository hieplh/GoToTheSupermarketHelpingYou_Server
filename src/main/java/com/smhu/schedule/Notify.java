package com.smhu.schedule;

import com.smhu.controller.OrderController;
import com.smhu.iface.IOrder;
import com.smhu.system.SystemTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Notify {

//    @Scheduled(fixedRate = 15 * 1000)
    public void removeOrder() {
        SystemTime systemTime = new SystemTime();
        systemTime.checkOrderOutOfTimeRelease();
    }
    
    @Scheduled(fixedRate = 20 * 1000)
    public void scanOrder() {
        IOrder orderListener = new OrderController().getOrderListener();
        orderListener.scanOrdesrReleaseToShippers();
    }

//    private void clear() {
//        if (!OrderController.mapOrderInQueue.isEmpty()) {
//            OrderController.mapOrderInQueue.clear();
//        }
//
//        if (!OrderController.mapOrderIsWaitingAccept.isEmpty()) {
//            OrderController.mapOrderIsWaitingAccept.clear();
//        }
//
//        if (!OrderController.mapCountOrderRelease.isEmpty()) {
//            OrderController.mapCountOrderRelease.clear();
//        }
//
////        if (!OrderController.mapOrderInProgress.isEmpty()) {
////            OrderController.mapOrderInProgress.clear();
////        }
//        if (!OrderController.mapOrdersShipperReject.isEmpty()) {
//            OrderController.mapOrdersShipperReject.clear();
//        }
//
//        if (!OrderController.mapOrderDeliveryForShipper.isEmpty()) {
//            OrderController.mapOrderDeliveryForShipper.clear();
//        }
//
//        if (!OrderController.mapOrderIsDone.isEmpty()) {
//            OrderController.mapOrderIsDone.clear();
//        }
//
////        if (!OrderController.mapOrderIsCancelInQueue.isEmpty()) {
////            OrderController.mapOrderIsCancelInQueue.clear();
////        }
//        if (!OrderController.mapOrderIsCancel.isEmpty()) {
//            OrderController.mapOrderIsCancel.clear();
//        }
//
////        if (!ShipperController.listAvailableShipper.isEmpty()) {
////            ShipperController.listAvailableShipper.clear();
////        }
////
//        for (Iterator<Shipper> it = ShipperController.mapInProgressShipper.iterator(); it.hasNext();) {
//            Shipper shipper = it.next();
//            ShipperController.mapAvailableShipper.add(shipper);
//            it.remove();
//        }
//    }
}
