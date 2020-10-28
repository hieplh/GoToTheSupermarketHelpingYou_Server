package com.smhu.controller;

import com.smhu.google.matrixobj.DistanceMatrixObject;
import com.smhu.google.matrixobj.ElementObject;
import com.smhu.helper.GsonHelper;
import com.smhu.response.shipper.OrderDelivery;
import com.smhu.url.UrlConnection;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.smhu.iface.IStatus;
import com.smhu.order.Order;
import com.smhu.system.SystemTime;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static Map<String, String[]> mapLocationAvailableShipper = new HashMap();
    public static Map<String, String[]> mapLocationInProgressShipper = new HashMap();

    UrlConnection url;

    ShipperService service = new ShipperService();
    IStatus statusListener;

    @GetMapping("/shipper/{shipperId}/lat/{lat}/lng/{lng}")
    public ResponseEntity<?> getNewOrders(@PathVariable("shipperId") String id,
            @PathVariable("lat") String lat, @PathVariable("lng") String lng) {
        url = new UrlConnection();
        try {
            if (OrderController.mapOrderIsWaitingRelease.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.OK);
            }

            if (mapLocationInProgressShipper.containsKey(id)) {
                return new ResponseEntity<>(HttpStatus.OK);
            }

            String[] shipperLocation = {lat, lng};
            mapLocationInProgressShipper.put(id, shipperLocation);
            Map<String[][], List<Order>> ordersLocation = service.getOrderLocation(OrderController.mapOrderIsWaitingRelease, shipperLocation);

            DistanceMatrixObject distanceObj = GsonHelper.gson.fromJson(new InputStreamReader(
                    url.openConnection(shipperLocation, ordersLocation), "utf-8"), DistanceMatrixObject.class);

            List<ElementObject> listElments = service.getListElements(distanceObj);
            List<String> listDistanceString = service.getListDistance(listElments, "text");
            List<String> listDistanceValue = service.getListDistance(listElments, "value");

            OrderDelivery[] arrOrders = service.filterOrderNearShipper(listDistanceString, listDistanceValue, ordersLocation);
            preProcessOrderRelease(arrOrders, id);
            return new ResponseEntity<>(arrOrders, HttpStatus.OK);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
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

    class ShipperService {

        Map<String[][], List<Order>> getOrderLocation(Map<String, Order> mapOrders, String[] shipperLocation) {
            Map<String[][], List<Order>> map = new HashMap<>();
            for (Order order : mapOrders.values()) {
                String lat = MarketController.mapMarket.get(order.getMarket()).getLat();
                String lng = MarketController.mapMarket.get(order.getMarket()).getLng();

                if (splitLocation(shipperLocation[0]).equals(splitLocation(lat))) {
                    if (splitLocation(shipperLocation[1]).equals(splitLocation(lng))) {
                        String[][] tmp = null;
                        for (String[][] arr : map.keySet()) {
                            if (arr[0][0].equals(lat)) {
                                if (arr[0][1].equals(lng)) {
                                    tmp = arr;
                                }
                            }
                        }
                        List<Order> listTmp = null;
                        if (tmp == null) {
                            tmp = new String[1][2];
                            tmp[0][0] = lat;
                            tmp[0][1] = lng;
                            listTmp = new ArrayList<>();
                        } else {
                            listTmp = map.get(tmp);
                        }
                        listTmp.add(order);
                        map.put(tmp, listTmp);
                    }
                }
            }
            return map;
        }

        String splitLocation(String location) {
            return location.split("\\.")[0];
        }

        List<String> getListDistance(List<ElementObject> list, String type) {
            List<String> listDistanceValues = new ArrayList<>();
            for (ElementObject element : list) {
                listDistanceValues.add(element.getDistance().get(type));
            }
            return listDistanceValues;
        }

        OrderDelivery[] filterOrderNearShipper(List<String> listDistanceString, List<String> listDistanceValue,
                Map<String[][], List<Order>> ordersLocation) {
            OrderDelivery[] orders = null;
            int index = 0;

            int tmp = Integer.parseInt(listDistanceValue.get(0));
            for (int i = 1; i < listDistanceValue.size(); i++) {
                if (tmp > Integer.parseInt(listDistanceValue.get(i))) {
                    index = i;
                    tmp = Integer.parseInt(listDistanceValue.get(i));
                }
            }

            int count = -1;
            for (Map.Entry<String[][], List<Order>> entry : ordersLocation.entrySet()) {
                if (++count == index) {
                    orders = new OrderDelivery[entry.getValue().size()];
                    for (int i = 0; i < entry.getValue().size(); i++) {
                        orders[i] = new OrderDelivery(listDistanceString.get(index),
                                Integer.parseInt(listDistanceValue.get(index)),
                                entry.getValue().get(i));
                    }
                }
            }
            return orders;
        }

        List<ElementObject> getListElements(DistanceMatrixObject obj) {
            return obj.getRows()[0].getElements();
        }
    }
}
