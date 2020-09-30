/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.schedule;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */
@Component
public class NotifyOrder {

    static LocalTime LOCAL_TIME;
    static List<OrderInfo> listOrders = new ArrayList<>();
    static Map<String, Integer> mapTimeDelivery = new HashMap<>();
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    static boolean infiniteFlag = false;

    public NotifyOrder() {
        if (listOrders.isEmpty()) {
            initList();
        }
        if (mapTimeDelivery.isEmpty()) {
            initMap();
        }
    }

    @Scheduled(cron = "0 7 * ? * *")
    public void pushOrder() throws InterruptedException {
        infiniteFlag = true;

        while (infiniteFlag) {
            System.out.println("The time is now " + dateFormat.format(new Date()));
            for (Map.Entry<String, Integer> order : mapTimeDelivery.entrySet()) {
                if (pushOrderForShipper(order.getValue())) {
                    System.out.println("Order " + order.getKey() + " is push");
                }
            }
            if (LocalTime.of(20, 20, 0).compareTo(LOCAL_TIME) <= -1) {
                infiniteFlag = false;
            } else {
                Thread.sleep(5 * 60 * 1000);
            }
        }
    }

    private void initList() {
        listOrders.add(new OrderInfo("1", "in queue", "18:00:00", "BigC"));
        listOrders.add(new OrderInfo("2", "in queue", "20:00:00", "BigC"));
        listOrders.add(new OrderInfo("3", "in queue", "19:50:00", "Coop-Food"));
        listOrders.add(new OrderInfo("4", "in queue", "19:55:00", "Bach Hoa Xanh"));
        listOrders.add(new OrderInfo("5", "in queue", "21:00:00", "Coop-Mart"));
    }

    private void initMap() {
        for (OrderInfo order : listOrders) {
            mapTimeDelivery.put(order.id, getTimeForShipper(order));
        }
    }

    private int getTimeForShipper(OrderInfo orderObj) {
        LocalTime timeDelivery = LocalTime.parse(orderObj.time);
        int timeDeliveryInt = timeDelivery.getHour() * 60 + timeDelivery.getMinute();

        int timeShopping = 30;
        int timeGoing = 30;
        int timeReceivingOrder = 5;

        return timeDeliveryInt - timeShopping - timeGoing - timeReceivingOrder;
    }

    private boolean pushOrderForShipper(int timeForShipper) {
        LOCAL_TIME = LocalTime.now(ZoneId.of("GMT+7"));
        int currentTime = LOCAL_TIME.getHour() * 60 + LOCAL_TIME.getMinute();
        return currentTime >= timeForShipper && currentTime <= timeForShipper + 15;
    }

    public class OrderInfo {

        String id;
        String status;
        String time;
        String mall;

        public OrderInfo(String id, String status, String time, String mall) {
            this.id = id;
            this.status = status;
            this.time = time;
            this.mall = mall;
        }
    }
}
