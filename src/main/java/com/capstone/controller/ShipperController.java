package com.capstone.controller;

import com.capstone.google.matrixobj.DistanceMatrixObject;
import com.capstone.google.matrixobj.ElementObject;
import com.capstone.helper.GsonHelper;
import com.capstone.order.Order;
import com.capstone.order.OrderDelivery;
import com.capstone.url.UrlConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.InputStreamReader;
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
            
            String[][] ordersLocation = getOrderLocation(OrderController.listOrderInProcess);
            String[] shipperLocation = {lat, lng};
            
            DistanceMatrixObject distanceObj = GsonHelper.gson.fromJson(new InputStreamReader(
                    url.openConnection(shipperLocation, ordersLocation), "utf-8"), DistanceMatrixObject.class);
            
            List<ElementObject> listElments = getListElements(distanceObj);
            OrderDelivery[] arrOrders = new OrderDelivery[OrderController.listOrderInProcess.size()];
            for (int i = 0; i < arrOrders.length; i++) {
                arrOrders[i] = new OrderDelivery(listElments.get(i).getDistance().get("text"), OrderController.listOrderInProcess.get(i));
            }
            
            return new ResponseEntity<>(arrOrders, HttpStatus.OK);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            Logger.getLogger(ShipperController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private String[][] getOrderLocation(List<Order> orders) {
        String[][] arr = new String[orders.size()][orders.size()];
        for (int i = 0 ; i < orders.size(); i++) {
            arr[i][0] = MarketController.mapMarket.get(orders.get(i).getMarket()).getLat();
            arr[i][1] = MarketController.mapMarket.get(orders.get(i).getMarket()).getLng();
        }
        return arr;
    }
    
    private List<ElementObject> getListElements(DistanceMatrixObject obj) {
        return obj.getRows()[0].getElements();
    }
}
