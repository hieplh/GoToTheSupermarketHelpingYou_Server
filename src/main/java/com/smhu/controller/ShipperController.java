package com.smhu.controller;

import com.smhu.google.matrixobj.DistanceMatrixObject;
import com.smhu.google.matrixobj.ElementObject;
import com.smhu.helper.GsonHelper;
import com.smhu.order.Order;
import com.smhu.order.OrderDelivery;
import com.smhu.url.UrlConnection;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
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

    public static UrlConnection url = new UrlConnection();

    @GetMapping("/shipper/{shipperId}/lat/{lat}/lng/{lng}")
    public ResponseEntity<?> getNewOrders(@PathVariable("shipperId") String id,
            @PathVariable("lat") String lat, @PathVariable("lng") String lng) {
        try {
            if (OrderController.listOrderInProcess.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.OK);
            }

            String[] shipperLocation = {lat, lng};
            mapLocationAvailableShipper.put(id, shipperLocation);
            Map<String[][], List<Order>> ordersLocation = getOrderLocation(OrderController.listOrderInProcess, shipperLocation);

            DistanceMatrixObject distanceObj = GsonHelper.gson.fromJson(new InputStreamReader(
                    url.openConnection(shipperLocation, ordersLocation), "utf-8"), DistanceMatrixObject.class);

            List<ElementObject> listElments = getListElements(distanceObj);
            
            OrderDelivery[] arrOrders = new OrderDelivery[OrderController.listOrderInProcess.size()];
            for (int i = 0; i < arrOrders.length; i++) {
                arrOrders[i] = new OrderDelivery(listElments.get(i).getDistance().get("text"),
                        Integer.parseInt(listElments.get(i).getDistance().get("value")),
                        OrderController.listOrderInProcess.get(i));
            }

            return new ResponseEntity<>(arrOrders, HttpStatus.OK);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            Logger.getLogger(ShipperController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String[][], List<Order>> getOrderLocation(List<Order> orders, String[] shipperLocation) {
        Map<String[][], List<Order>> map = new HashMap<>();
        for (Order order : orders) {
            String lat = MarketController.mapMarket.get(order.getMarket()).getLat();
            String lng = MarketController.mapMarket.get(order.getMarket()).getLng();

            if (splitLocation(shipperLocation[0]).equals(splitLocation(lat))) {
                if (splitLocation(shipperLocation[1]).equals(splitLocation(lng))) {
                    String[][] array = {{lat, lng}};
                    List<Order> tmp = map.get(array);
                    if (tmp == null) {
                        tmp = new ArrayList<>();
                    }
                    tmp.add(order);
                    map.put(array, tmp);
                }
            }
        }
        return map;
    }

    private String splitLocation(String location) {
        return location.split(".")[0];
    }

    private List<ElementObject> getListElements(DistanceMatrixObject obj) {
        return obj.getRows()[0].getElements();
    }
}
