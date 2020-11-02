package com.smhu.controller;

import com.smhu.response.shipper.OrderDelivery;
import com.smhu.account.Shipper;
import com.smhu.iface.IShipper;
import com.smhu.order.Order;
import com.smhu.request.shipper.ShipperRequest;
import com.smhu.system.SystemTime;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ShipperController {

    public static Map<Shipper, String[]> mapShipper = new HashMap();
    public static List<String> listInProgressShipper = new ArrayList();
    public static Map<Integer, Integer> mapMechanismReleaseOrder = new HashMap<>();

    private ShipperService service;
    private final IShipper shipperListener;

    public ShipperController() {
        shipperListener = new ShipperService();
    }

    @GetMapping("/shipper/{shipperId}/lat/{lat}/lng/{lng}")
    public ResponseEntity<?> setCurrentLocationOfShipper(@PathVariable("shipperId") String id,
            @PathVariable("lat") String lat, @PathVariable("lng") String lng) {
        try {
            service = new ShipperService();
            String[] shipperLocation = {lat, lng};
            Shipper shipper = service.getShipperAccount(id);
            mapShipper.put(shipper, shipperLocation);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/shipper/{shipperId}")
//    public ResponseEntity<?> getReleaseOrders(@PathVariable("shipperId") String id) {
//        try {
//            if (OrderController.mapOrderIsWaitingRelease.isEmpty()) {
//                return new ResponseEntity<>(null, HttpStatus.OK);
//            }
//
//            if (mapLocationInProgressShipper.containsKey(id)) {
//                return new ResponseEntity<>(HttpStatus.OK);
//            }
//            service = new ShipperService();
//            url = new UrlConnection();
//
//            Shipper shipper = service.getShipperAccount(id);
//            if (shipper == null) {
//                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
//            }
//
//            String[] shipperLocation = mapLocationAvailableShipper.get(shipper);
//            Map<String[][], List<Order>> locationOrders = service.groupOrders(shipper,
//                    OrderController.mapOrderIsWaitingRelease, shipperLocation);
//            DistanceMatrixObject distanceObj = GsonHelper.gson.fromJson(new InputStreamReader(
//                    url.openConnectionFromSourceToDestination(shipperLocation, locationOrders), "utf-8"), DistanceMatrixObject.class);
//
//            ExtractElementDistanceMatrixApi extract = new ExtractElementDistanceMatrixApi();
//            List<ElementObject> listElments = extract.getListElements(distanceObj);
//            List<String> listDistanceString = extract.getListDistance(listElments, "text");
//            List<String> listDistanceValue = extract.getListDistance(listElments, "value");
//
//            OrderDelivery[] arrOrders = service.filterOrderNearShipper(shipper, locationOrders, listDistanceString, listDistanceValue);
//            if (arrOrders != null) {
//                preProcessOrderRelease(arrOrders, id);
//                mapLocationInProgressShipper.put(shipper, shipperLocation);
//            }
//
//            return new ResponseEntity<>(arrOrders, HttpStatus.OK);
//        } catch (JsonIOException | JsonSyntaxException | IOException e) {
//            Logger.getLogger(ShipperController.class.getName()).log(Level.SEVERE, e.getMessage());
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    @PostMapping("/shipper/orders")
    public ResponseEntity<?> getReleaseOrders(@RequestBody ShipperRequest request) {
        service = new ShipperService();
        try {
            if (service.getShipperInProgress(request.getId()) != null) {
                return new ResponseEntity<>(HttpStatus.OK);
            }

            Shipper shipper = service.getShipperAccount(request.getId());
            if (shipper == null) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }

            List<OrderDelivery> listResult = new ArrayList<>();
            for (String orderId : request.getOrders()) {
                listResult.add(OrderController.mapOrderDeliveryForShipper.get(orderId));
            }
            return new ResponseEntity<>(listResult, HttpStatus.OK);
        } catch (Exception e) {
            Logger.getLogger(ShipperController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void preProcessOrderRelease(OrderDelivery[] arrOrders, String shipperId) {
        for (OrderDelivery order : arrOrders) {
            Order obj = OrderController.mapOrderIsWaitingRelease.remove(order.getOrder().getId());
            obj.setShipper(shipperId);
            OrderController.mapOrderIsWaitingAccept.put(obj, SystemTime.SYSTEM_TIME + (20 * 1000));
        }
    }

    public IShipper getShipperListener() {
        return shipperListener;
    }

    class ShipperService implements IShipper {

        @Override
        public Shipper getShipperAccount(String id) {
            return mapShipper.keySet()
                    .stream()
                    .filter(shipper -> shipper.getId().equals(id))
                    .findFirst()
                    .orElseThrow(null);
        }

        String getShipperInProgress(String shipperId) {
            return listInProgressShipper.stream()
                    .filter(id -> id.equals(id))
                    .findFirst()
                    .orElseThrow(null);
        }
        
        String checkOrderShipperRejected(List<String> listOrderReject, String orderId) {
            return listOrderReject.stream()
                    .filter(id -> orderId.equals(id))
                    .findFirst()
                    .orElse(null);
        }

//        Map<String[][], List<Order>> groupOrders(Shipper shipper, Map<String, Order> mapOrders, String[] shipperLocation) {
//            Map<String[][], List<Order>> map = new HashMap<>();
//            List<String> listOrderReject = OrderController.mapOrdersShipperReject.get(shipper.getId());
//            for (Order order : mapOrders.values()) {
//                boolean flag = checkOrderShipperRejected(listOrderReject, order.getId()) == null;
//                if (flag) {
//                    String lat = MarketController.mapMarket.get(order.getMarket()).getLat();
//                    String lng = MarketController.mapMarket.get(order.getMarket()).getLng();
//
//                    if (splitLocation(shipperLocation[0]).equals(splitLocation(lat))) {
//                        if (splitLocation(shipperLocation[1]).equals(splitLocation(lng))) {
//                            String[][] tmp = null;
//                            for (String[][] arr : map.keySet()) {
//                                if (arr[0][0].equals(lat)) {
//                                    if (arr[0][1].equals(lng)) {
//                                        tmp = arr;
//                                    }
//                                }
//                            }
//                            List<Order> listTmp = null;
//                            if (tmp == null) {
//                                tmp = new String[1][2];
//                                tmp[0][0] = lat;
//                                tmp[0][1] = lng;
//                                listTmp = new ArrayList<>();
//                            } else {
//                                listTmp = map.get(tmp);
//                            }
//                            listTmp.add(order);
//                            map.put(tmp, listTmp);
//                        }
//                    }
//                }
//            }
//            return map;
//        }
//        double checkCountOrderRelease(String orderId) {
//            int count = OrderController.mapCountOrderRelease.getOrDefault(orderId, 0) + 1;
//            TreeMap<Integer, Double> sortMechanismReleaseOrder = new TreeMap<>(mapMechanismReleaseOrder);
//            for (Integer integer : sortMechanismReleaseOrder.keySet()) {
//                if (count <= integer) {
//                    return ShipperController.mapMechanismReleaseOrder.get(integer);
//                }
//            }
//            return 0;
//        }
        int getTheShortestDistance(List<String> listDistanceValue) {
            int index = 0;
            int tmp = Integer.parseInt(listDistanceValue.get(0));
            for (int i = 1; i < listDistanceValue.size(); i++) {
                if (tmp > Integer.parseInt(listDistanceValue.get(i))) {
                    index = i;
                    tmp = Integer.parseInt(listDistanceValue.get(i));
                }
            }
            return index;
        }

//        OrderDelivery[] filterOrderNearShipper(Shipper shipper, Map<String[][], List<Order>> ordersLocation,
//                List<String> listDistanceString, List<String> listDistanceValue) {
//            OrderDelivery[] orders = null;
//            int index = getTheShortestDistance(listDistanceValue);
//
//            int position = -1;
//            double range = 0;
//            for (Map.Entry<String[][], List<Order>> entry : ordersLocation.entrySet()) {
//                if (++position == index) {
//                    int maxOrderShipperReceive = shipper.getMaxOrder();
//                    int tmpIndex = -1;
//                    for (Order order : entry.getValue()) {
//                        if (++tmpIndex <= maxOrderShipperReceive - 1) {
//                            if ((range = checkCountOrderRelease(order.getId())) == 0) {
//                                return null;
//                            }
//                        } else {
//                            break;
//                        }
//                    }
//
//                    if (Integer.parseInt(listDistanceValue.get(index)) > range) {
//                        return null;
//                    }
//
//                    orders = new OrderDelivery[maxOrderShipperReceive];
//                    for (int i = 0; i < entry.getValue().size(); i++) {
//                        if (i > maxOrderShipperReceive - 1) {
//                            break;
//                        }
//
//                        int count = OrderController.mapCountOrderRelease.getOrDefault(entry.getValue().get(i).getId(), 0) + 1;
//                        OrderController.mapCountOrderRelease.put(entry.getValue().get(i).getId(), count);
//
//                        orders[i] = new OrderDelivery(listDistanceString.get(index),
//                                Integer.parseInt(listDistanceValue.get(index)),
//                                entry.getValue().get(i));
//                    }
//                    break;
//                }
//            }
//            return orders;
//        }
    }
}
