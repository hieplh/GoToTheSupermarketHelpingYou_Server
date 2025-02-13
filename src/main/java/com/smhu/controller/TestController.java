package com.smhu.controller;

import com.smhu.GototheSupermarketHelpingYouApplication;
import com.smhu.account.Shipper;
import com.smhu.core.CoreFunctions;
import com.smhu.schedule.Scan;
import com.smhu.system.SystemTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@CrossOrigin
@RequestMapping("/api")
public class TestController {
    
    private void clear() {
        if (!OrderController.mapOrderInQueue.isEmpty()) {
            OrderController.mapOrderInQueue.clear();
        }

        if (!OrderController.mapOrderIsWaitingAccept.isEmpty()) {
            OrderController.mapOrderIsWaitingAccept.clear();
        }

        if (!OrderController.mapOrderDeliveryForShipper.isEmpty()) {
            OrderController.mapOrderDeliveryForShipper.clear();
        }
        
        if (!OrderController.mapCountOrderRelease.isEmpty()) {
            OrderController.mapCountOrderRelease.clear();
        }

        if (!OrderController.mapOrderInProgress.isEmpty()) {
            OrderController.mapOrderInProgress.clear();
        }
        
        if (!OrderController.mapOrdersShipperReject.isEmpty()) {
            OrderController.mapOrdersShipperReject.clear();
        }

        if (!OrderController.mapOrderIsDone.isEmpty()) {
            OrderController.mapOrderIsDone.clear();
        }

        if (!OrderController.mapOrderIsCancelInQueue.isEmpty()) {
            OrderController.mapOrderIsCancelInQueue.clear();
        }
        
        if (!OrderController.mapOrderIsCancel.isEmpty()) {
            OrderController.mapOrderIsCancel.clear();
        }

//        if (!ShipperController.listAvailableShipper.isEmpty()) {
//            ShipperController.listAvailableShipper.clear();
//        }
//
        if (!ShipperController.mapShipperOrdersInProgress.isEmpty()) {
            ShipperController.mapShipperOrdersInProgress.clear();
        }
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

    @GetMapping("/on_off_scan")
    public ResponseEntity<?> enableScan() {
        String s;
        if (Scan.ENABLE_SCAN_SCHEDULE) {
            Scan.ENABLE_SCAN_SCHEDULE = false;
            s = "Scan is Off";
        } else {
            Scan.ENABLE_SCAN_SCHEDULE = true;
            s = "Scan is on";
        }
        return new ResponseEntity<>(s, HttpStatus.OK);
    }
    
    @GetMapping("/on_off_remote")
    public ResponseEntity<?> enableRemote() {
        String s;
        if (Scan.ENABLE_REMOTE_SCHEDULE) {
            Scan.ENABLE_REMOTE_SCHEDULE = false;
            s = "Remote is Off";
        } else {
            Scan.ENABLE_REMOTE_SCHEDULE = true;
            s = "Remote is on";
        }
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

//    @GetMapping("/scan")
//    public ResponseEntity<?> scanOrder() {
//        IOrder orderListener = new OrderController().getOrderListener();
//        orderListener.scanOrdesrReleaseToShippers();
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
    @GetMapping("/new_scan_shipper")
    public ResponseEntity<?> scanShipperNew() {
        CoreFunctions coreFunctions = new CoreFunctions();
        coreFunctions.scanShippers();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/new_scan_order")
    public ResponseEntity<?> scanOrderNew() {
        CoreFunctions coreFunctions = new CoreFunctions();
        coreFunctions.scanOrder();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
