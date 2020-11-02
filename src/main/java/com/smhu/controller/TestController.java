package com.smhu.controller;

import com.smhu.iface.IOrder;
import com.smhu.system.SystemTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequestMapping("/api")
public class TestController {

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
