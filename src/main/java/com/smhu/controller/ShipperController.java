package com.smhu.controller;

import static com.smhu.controller.OrderController.mapOrderIsWaitingAccept;

import com.smhu.response.shipper.OrderDelivery;
import com.smhu.account.Shipper;
import com.smhu.core.CoreFunctions;
import com.smhu.dao.ShipperDAO;
import com.smhu.helper.SyncHelper;
import com.smhu.iface.ICore;
import com.smhu.order.Order;
import java.util.ArrayList;

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

    @GetMapping("/shipper/{shipperId}/reject")
    public ResponseEntity<?> rejectOrder(@PathVariable("shipperId") String shipperId) {
        try {
            Shipper shipper = service.getShipper(shipperId);
            if (shipper == null) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
            if (checkShipperIsInProgress(shipper)) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }

            List<String> listOrdersBelongToShipper = mapShipperOrdersInProgress.get(shipper.getUsername());
            if (listOrdersBelongToShipper != null) {
                ICore coreListener = new CoreFunctions();
                service.changeStatusOfShipper(shipper.getUsername());
                listOrdersBelongToShipper.forEach((String id) -> {
                    Order order = getOrder(id);
                    removeOrderWaitingAccept(order);
                    updateMapOrderShipperReject(shipper, order);
                    coreListener.filterOrder(order);
                });
                OrderController.mapOrderDeliveryForShipper.remove(shipper.getUsername());
                mapShipperOrdersInProgress.remove(shipper.getUsername());
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            Logger.getLogger(ShipperController.class.getName()).log(Level.SEVERE, "Reject orders: {0}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            if (list == null) {
                list = reSyncOrder(shipper);
            }
            if (list != null) {
                System.out.println("Shipper: " + shipperId + " received Orders");
                for (OrderDelivery orderDelivery : list) {
                    System.out.println(orderDelivery);
                }
                System.out.println("");
            }

            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            Logger.getLogger(ShipperController.class.getName()).log(Level.SEVERE, "Get release orders: {0}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/shipper/{shipperId}/pre-order")
    public ResponseEntity<?> getPreOrders(@PathVariable("shipperId") String shipperId) {
        try {
            Shipper shipper = service.getShipper(shipperId);
            if (shipper == null) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }

            List<Order> listPreOrders = OrderController.mapOrderWaitingAfterShopping.getOrDefault(shipper.getUsername(), null);

            if (listPreOrders == null) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }

            SyncHelper sync = new SyncHelper();
            List<OrderDelivery> result = null;
            List<String> listIdOrdersBelongToShipper = null;
            for (Order preOrder : listPreOrders) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                if (listIdOrdersBelongToShipper == null) {
                    listIdOrdersBelongToShipper = new ArrayList();
                }
                result.add(sync.syncOrderSystemToOrderDelivery(preOrder));
                listIdOrdersBelongToShipper.add(preOrder.getId());
            }
            mapShipperOrdersInProgress.put(shipper.getUsername(), listIdOrdersBelongToShipper);

            if (mapAvailableShipper.containsKey(shipper.getUsername())) {
                service.changeStatusOfShipper(shipper.getUsername());
            }

            if (result != null) {
                System.out.println("Shipper: " + shipperId + " received Orders");
                for (OrderDelivery orderDelivery : result) {
                    System.out.println(orderDelivery);
                }
                System.out.println("");
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Logger.getLogger(ShipperController.class.getName()).log(Level.SEVERE, "Get release orders: {0}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<OrderDelivery> reSyncOrder(Shipper shipper) {
        List<OrderDelivery> result = null;
        SyncHelper sync = new SyncHelper();
        List<String> list = mapShipperOrdersInProgress.get(shipper.getUsername());
        if (list != null) {
            for (String id : list) {
                if (result == null) {
                    result = new ArrayList();
                }
                Order order = OrderController.mapOrderInProgress.get(id);
                if (order != null) {
                    result.add(sync.syncOrderSystemToOrderDelivery(order));
                }
            }
        }
        return result;
    }

    private boolean checkShipperIsInProgress(Shipper shipper) {
        for (Map.Entry<String, Order> entry : OrderController.mapOrderInProgress.entrySet()) {
            if (entry.getValue().getShipper().equals(shipper)) {
                return true;
            }
        }
        return false;
//        return OrderController.mapOrderInProgress.entrySet().stream().anyMatch((entry) -> (entry.getValue().getShipper().equals(shipper)));
    }

    private Order getOrder(String id) {
        return mapOrderIsWaitingAccept.keySet()
                .stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void removeOrderWaitingAccept(Order order) {
        mapOrderIsWaitingAccept.remove(order);
    }

    private void updateMapOrderShipperReject(Shipper shipper, Order order) {
        List<String> list = OrderController.mapOrdersShipperReject.get(shipper.getUsername());
        if (list == null) {
            list = new ArrayList<>();
            OrderController.mapOrdersShipperReject.put(shipper.getUsername(), list);
        }
        list.add(order.getId());
    }
}
