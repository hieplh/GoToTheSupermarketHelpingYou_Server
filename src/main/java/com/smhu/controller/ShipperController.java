package com.smhu.controller;

import com.smhu.response.shipper.OrderDelivery;
import com.smhu.account.Shipper;
import com.smhu.dao.ShipperDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ShipperController {

    public final static Map<String, Shipper> mapAvailableShipper = new HashMap<>();
    public final static Map<String, Shipper> mapInProgressShipper = new HashMap();
    public final static Map<String, List<String>> mapShipperOrdersInProgress = new HashMap<>();
    public static Map<Integer, Integer> mapMechanismReleaseOrder = new HashMap<>();

    private final ShipperDAO service;

    public ShipperController() {
        service = new ShipperDAO();
    }

    @GetMapping("/shipper/{shipperId}/lat/{lat}/lng/{lng}/{token}")
    public ResponseEntity<?> setCurrentLocationOfShipper(@PathVariable("shipperId") String id,
            @PathVariable("lat") String lat, @PathVariable("lng") String lng, @PathVariable("token") String token) {
        try {
            Shipper shipper = service.getShipper(id);
            if (shipper == null) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
            }
            shipper.setLat(lat);
            shipper.setLng(lng);
            shipper.setTokenFCM(token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            Logger.getLogger(ShipperController.class.getName()).log(Level.SEVERE, "Set current location: {0}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/shipper/{shipperId}")
    public ResponseEntity<?> getReleaseOrders(@PathVariable("shipperId") String shipperId) {
        try {
            Shipper shipper = service.getShipper(shipperId);
            if (shipper == null) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
            List<OrderDelivery> list = OrderController.mapOrderDeliveryForShipper.getOrDefault(shipperId, null);
            if (list != null) {
                System.out.println("Shipper: " + shipperId + " received Orders");
                for (OrderDelivery orderDelivery : list) {
                    System.out.println(orderDelivery);
                }
                System.out.println("");
            }
            return new ResponseEntity<>(OrderController.mapOrderDeliveryForShipper.getOrDefault(shipperId, null), HttpStatus.OK);
        } catch (Exception e) {
            Logger.getLogger(ShipperController.class.getName()).log(Level.SEVERE, "Get release orders: {0}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
