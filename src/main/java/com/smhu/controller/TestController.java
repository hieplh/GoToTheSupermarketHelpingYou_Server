package com.smhu.controller;

import com.smhu.GototheSupermarketHelpingYouApplication;
import com.smhu.account.Shipper;
import com.smhu.iface.IOrder;
import com.smhu.system.SystemTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequestMapping("/api")
public class TestController {

    private void clear() {
        if (!OrderController.mapOrderInQueue.isEmpty()) {
            OrderController.mapOrderInQueue.clear();
        }

        if (!OrderController.mapOrderIsWaitingAccept.isEmpty()) {
            OrderController.mapOrderIsWaitingAccept.clear();
        }

        if (!OrderController.mapCountOrderRelease.isEmpty()) {
            OrderController.mapCountOrderRelease.clear();
        }

//        if (!OrderController.mapOrderInProgress.isEmpty()) {
//            OrderController.mapOrderInProgress.clear();
//        }
        if (!OrderController.mapOrdersShipperReject.isEmpty()) {
            OrderController.mapOrdersShipperReject.clear();
        }

        if (!OrderController.mapOrderDeliveryForShipper.isEmpty()) {
            OrderController.mapOrderDeliveryForShipper.clear();
        }

        if (!OrderController.mapOrderIsDone.isEmpty()) {
            OrderController.mapOrderIsDone.clear();
        }

//        if (!OrderController.mapOrderIsCancelInQueue.isEmpty()) {
//            OrderController.mapOrderIsCancelInQueue.clear();
//        }
        if (!OrderController.mapOrderIsCancel.isEmpty()) {
            OrderController.mapOrderIsCancel.clear();
        }

//        if (!ShipperController.listAvailableShipper.isEmpty()) {
//            ShipperController.listAvailableShipper.clear();
//        }
//
        List<String> keyRemoved = new ArrayList<>(ShipperController.mapInProgressShipper.keySet());
        for (String key : keyRemoved) {
            Shipper shipper = ShipperController.mapInProgressShipper.remove(key);
            ShipperController.mapAvailableShipper.put(key, shipper);
        }
    }

    @GetMapping("/reload")
    public ResponseEntity<?> reload() {
        clear();
        new GototheSupermarketHelpingYouApplication().init();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/remove")
    public ResponseEntity<?> removeOrder() {
        SystemTime systemTime = new SystemTime();
        systemTime.checkOrderOutOfTimeRelease();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/scan")
    public ResponseEntity<?> scanOrder() {
        IOrder orderListener = new OrderController().getOrderListener();
        orderListener.scanOrdesrReleaseToShippers();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
