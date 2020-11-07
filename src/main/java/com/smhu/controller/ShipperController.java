package com.smhu.controller;

import com.smhu.response.shipper.OrderDelivery;
import com.smhu.account.Shipper;
import com.smhu.iface.IShipper;
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
    public static Map<Integer, Integer> mapMechanismReleaseOrder = new HashMap<>();

    private final ShipperService service;
    private final IShipper shipperListener;

    public ShipperController() {
        service = new ShipperService();
        shipperListener = new ShipperService();
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
            System.out.println(shipper);
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
                for (OrderDelivery orderDelivery : list) {
                    System.out.println(orderDelivery);
                }
            }
            return new ResponseEntity<>(OrderController.mapOrderDeliveryForShipper.getOrDefault(shipperId, null), HttpStatus.OK);
        } catch (Exception e) {
            Logger.getLogger(ShipperController.class.getName()).log(Level.SEVERE, "Get release orders: {0}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public IShipper getShipperListener() {
        return shipperListener;
    }

    class ShipperService implements IShipper {

        @Override
        public Shipper getShipper(String id) {
            Shipper shipper = getAvailableShipperAccount(id);
            if (shipper == null) {
                shipper = getInProgressShipperAccount(id);
                if (shipper == null) {
                    return null;
                }
            }
            return shipper;
        }

        @Override
        public void addShipper(Shipper shipper) {
            if (mapInProgressShipper.containsKey(shipper.getId())) {
                return;
            }
            mapAvailableShipper.put(shipper.getId(), shipper);

        }

        @Override
        public void changeStatusOfShipper(String shipperId) {
            if (mapAvailableShipper.containsKey(shipperId)) {
                Shipper shipper = mapAvailableShipper.remove(shipperId);
                mapInProgressShipper.put(shipperId, shipper);
            } else {
                Shipper shipper = mapInProgressShipper.remove(shipperId);
                mapAvailableShipper.put(shipperId, shipper);
            }
        }

//        @Override
//        public void addShipperAccountAvailable(Shipper shipper) {
//            mapAvailableShipper.put(shipper.getId(), shipper);
//        }
//
//        @Override
//        public void addShipperAccountInProgress(Shipper shipper) {
//            mapInProgressShipper.put(shipper.getId(), shipper);
//        }
        Shipper getAvailableShipperAccount(String id) {
            return mapAvailableShipper.getOrDefault(id, null);
        }

        Shipper getInProgressShipperAccount(String id) {
            return mapInProgressShipper.getOrDefault(id, null);
        }

//        String getShipperInProgress(String shipperId) {
//            return listInProgressShipper.stream()
//                    .filter(id -> id.equals(id))
//                    .findFirst()
//                    .orElseThrow(null);
//        }
//        String checkOrderShipperRejected(List<String> listOrderReject, String orderId) {
//            return listOrderReject.stream()
//                    .filter(id -> orderId.equals(id))
//                    .findFirst()
//                    .orElse(null);
//        }
//
//        int getTheShortestDistance(List<String> listDistanceValue) {
//            int index = 0;
//            int tmp = Integer.parseInt(listDistanceValue.get(0));
//            for (int i = 1; i < listDistanceValue.size(); i++) {
//                if (tmp > Integer.parseInt(listDistanceValue.get(i))) {
//                    index = i;
//                    tmp = Integer.parseInt(listDistanceValue.get(i));
//                }
//            }
//            return index;
//        }
    }
}
