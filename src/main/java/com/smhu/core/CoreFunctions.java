package com.smhu.core;

import com.google.firebase.messaging.FirebaseMessagingException;

import static com.smhu.controller.OrderController.mapCountOrderRelease;
import static com.smhu.controller.OrderController.mapOrderDeliveryForShipper;
import static com.smhu.controller.OrderController.mapOrderInQueue;
import static com.smhu.controller.OrderController.mapOrderIsWaitingAccept;

import com.smhu.account.Shipper;
import com.smhu.callable.TimeDelivery;
import com.smhu.callable.TimeGoingToMarket;
import com.smhu.callable.TimeShopping;
import com.smhu.comparator.SortByHighActive;
import com.smhu.comparator.SortByTimeDelivery;
import com.smhu.controller.MarketController;
import com.smhu.controller.OrderController;
import com.smhu.controller.ShipperController;
import com.smhu.dao.OrderDAO;
import com.smhu.entity.Market;
import com.smhu.google.Firebase;
import com.smhu.helper.DateTimeHelper;
import com.smhu.helper.ExtractRangeInMechanism;
import com.smhu.iface.ICore;
import com.smhu.iface.IOrder;
import com.smhu.iface.IShipper;
import com.smhu.order.Order;
import com.smhu.response.shipper.OrderDelivery;
import com.smhu.system.SystemTime;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoreFunctions implements ICore {

    public static final Map<String, List<String>> mapFilterOrders = new HashMap<>(); // id Market - id Order
    public static final Map<String, List<String>> mapFilterShippers = new HashMap<>(); // id Market - id Shipper

    public static Map<String, Integer> AVG_TIME_TRAVEL_TO_MARKET = null;

    private final IShipper shipperListener;
    private final IOrder orderListener;

    private final String TRUE = "true";
    private final String FALSE = "false";

    public CoreFunctions() {
        this.shipperListener = new ShipperController().getShipperListener();
        this.orderListener = new OrderDAO();
    }

    public void initMapFilter() {
        if (!mapFilterOrders.isEmpty()) {
            mapFilterOrders.clear();
        }
        if (!mapFilterShippers.isEmpty()) {
            mapFilterShippers.isEmpty();
        }
        for (String key : MarketController.mapMarket.keySet()) {
            mapFilterOrders.put(key, null);
            mapFilterShippers.put(key, null);
        }
    }

    private double calculateDistance(double lat1, double lat2, double lng1, double lng2) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    @Override
    public void scanShippers() {
        synchronized (mapFilterShippers) {
            System.out.println("SCAN SHIPPER");

            mapFilterShippers.entrySet().forEach((entry) -> {
                Market market = MarketController.mapMarket.get(entry.getKey());
                List<String> list = entry.getValue();
                for (Map.Entry<String, Shipper> shipper : ShipperController.mapAvailableShipper.entrySet()) {
                    if (shipper.getValue().getLat() != null && shipper.getValue().getLng() != null) {
                        double lat1 = Double.parseDouble(market.getLat());
                        double lng1 = Double.parseDouble(market.getLng());
                        double lat2 = Double.parseDouble(shipper.getValue().getLat());
                        double lng2 = Double.parseDouble(shipper.getValue().getLng());
                        double distance = calculateDistance(lat1, lat2, lng1, lng2);

                        if (distance + 700 <= (double) ExtractRangeInMechanism.getTheShortesRangeInMechanism()) {
                            if (list == null) {
                                list = new ArrayList<>();
                                entry.setValue(list);
                            }
                            if (!list.contains(shipper.getKey())) {
                                list.add(shipper.getKey());
                            }
                        } else {
                            if (list != null) {
                                list.remove(shipper.getKey());
                            }
                        }
                    }
                }
            });

//            for (String market : mapFilterShippers.keySet()) {
//                System.out.println("Market: " + market);
//                List<String> list = mapFilterShippers.get(market);
//                if (list != null) {
//                    for (String shipper : list) {
//                        System.out.println("Shipper: " + shipper);
//                    }
//                }
//                System.out.println("");
//            }
        }
    }

    private boolean checkOrderDeliveryForShipper(Shipper shipper, Order order) {
        List<OrderDelivery> listDelivery = OrderController.mapOrderDeliveryForShipper.get(shipper.getId());
        if (listDelivery == null) {
            return false;
        }

        if (listDelivery.size() < shipper.getMaxOrder()) {
            order.setShipper(shipper.getId());

            List<Order> tmpList = new ArrayList();
            tmpList.add(order);
            for (OrderDelivery o : listDelivery) {
                for (Map.Entry<Order, Long> entry : OrderController.mapOrderIsWaitingAccept.entrySet()) {
                    if (entry.getKey().getId().equals(o.getId())) {
                        tmpList.add(entry.getKey());
                    }
                }
            }
            try {
                TimeDelivery timeDelivery = new TimeDelivery(tmpList);
                timeDelivery.call();
            } catch (Exception e) {
                Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Filter Order: {0}", e.getMessage());
            }

            preProcessOrderRelease(shipper, tmpList);
            return true;
        }
        return false;
    }

    private boolean checkOrderInProgress(List<Order> listOrders, Shipper shipper, Order order) {
        List<Order> tmpList = null;
        for (int i = 0; i < listOrders.size(); i++) {
            Order tmp = listOrders.get(i);
            if (tmp.getShipper().equals(shipper.getId())) {
                if (tmp.getStatus() < 23) {
                    if (tmpList == null) {
                        tmpList = new ArrayList();
                    }
                    tmpList.add(tmp);
                }
            }
        }

        if (tmpList != null && tmpList.size() < shipper.getMaxOrder()) {
            order.setShipper(shipper.getId());
            order.setStatus(listOrders.get(0).getStatus());
            
            try {
                orderListener.updatetOrder(order);
            } catch (ClassNotFoundException | SQLException e) {
                Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Filter Order - Update DB: {0}", e.getMessage());
            }
            
            OrderController.mapOrderInProgress.put(order.getId(), order);
            tmpList.add(order);

            try {
                TimeDelivery timeDelivery = new TimeDelivery(tmpList);
                timeDelivery.call();
            } catch (Exception e) {
                Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Filter Order - Check In Progress: {0}", e.getMessage());
            }

            preProcessOrderRelease(shipper, tmpList);
            return true;
        }
        return false;
    }

    @Override
    public void filterOrder(Order order) {
        List<String> listIdShipper = mapFilterShippers.get(order.getMarket());
        if (listIdShipper != null) {
            List<String> listShipper = null;
            for (String id : listIdShipper) {
                Shipper shipper = ShipperController.mapInProgressShipper.get(id);
                if (shipper != null) {
                    if (listShipper == null) {
                        listShipper = new ArrayList();
                    }
                    listShipper.add(shipper.getId());
                }
            }

            if (listShipper != null) {
                listShipper.sort(new SortByHighActive());

                for (String id : listShipper) {
                    boolean flag = false;
                    Shipper shipper = ShipperController.mapInProgressShipper.get(id);

                    List<Order> listOrders = null;
                    if (!OrderController.mapOrderInProgress.isEmpty()) {
                        listOrders = new ArrayList(OrderController.mapOrderInProgress.values());
                    }

                    if (listOrders != null) {
                        flag = checkOrderInProgress(listOrders, shipper, order);
                        if (!flag) {
                            flag = checkOrderDeliveryForShipper(shipper, order);
                        }
                    } else {
                        flag = checkOrderDeliveryForShipper(shipper, order);
                    }

                    if (flag) {
                        sendNotificationOrderToShipper(shipper.getTokenFCM(), TRUE);
                        return;
                    }
                } // end for
            }
        }

        Market market = MarketController.mapMarket.get(order.getMarket());
        List<String> list = mapFilterOrders.get(market.getId());
        if (list == null) {
            list = new ArrayList<>();
            mapFilterOrders.put(market.getId(), list);
        }
        list.add(order.getId());
    }

    private int checkTimeReleaseOrderForShipper(int time) {
        return time >= 180 ? time + 30 : (180 - time) <= 30 ? time + 30 : 180;
    }

    private void checkShipperIsAvailable(List<String> listIdShippers) {
        for (int i = listIdShippers.size() - 1; i >= 0; i--) {
            if (!ShipperController.mapAvailableShipper.containsKey(listIdShippers.get(i))) {
                listIdShippers.remove(i);
            }
        }
    }

    private List<Order> checkConditionOrderIsHasRelease(List<String> listIdOrders, Shipper shipper) {
        if (listIdOrders == null) {
            return null;
        }

        List<Order> listOrders = new ArrayList();
        listIdOrders.forEach((orderId) -> {
            listOrders.add(OrderController.mapOrderInQueue.get(orderId));
        });
        listOrders.sort(new SortByTimeDelivery());

        int count = 0;
        LocalTime releaseTime;
        List<Order> listOrderResult = null;
        DateTimeHelper helper = new DateTimeHelper();
        LocalTime curTime = LocalTime.now(ZoneId.of("GMT+7"));
        for (Order order : listOrders) {
            if (count >= shipper.getMaxOrder()) {
                break;
            }
            releaseTime = order.getTimeDelivery().toLocalTime();
            if (helper.parseTimeToMinute(releaseTime) - helper.parseTimeToMinute(curTime) <= (4 * 60) + (count * 30)) {
                if (listOrderResult == null) {
                    listOrderResult = new ArrayList();
                }
                listOrderResult.add(order);
                count++;
            }
        }
        return listOrderResult;
    }

    private void preProcessOrderRelease(Shipper shipper, List<Order> orders) {
        List<OrderDelivery> result = new ArrayList();
        for (Order order : orders) {
            Order removedOrder = mapOrderInQueue.remove(order.getId());
            if (removedOrder != null) {
                removedOrder.setShipper(shipper.getId());
                mapOrderIsWaitingAccept.put(removedOrder, SystemTime.SYSTEM_TIME + (20 * 1000));
                int numRelease = mapCountOrderRelease.getOrDefault(removedOrder.getId(), 0);
                mapCountOrderRelease.put(removedOrder.getId(), numRelease + 1);
            } else {
                removedOrder = order;
            }

            result.add(new OrderDelivery(removedOrder));
        }

        if (ShipperController.mapAvailableShipper.get(shipper.getId()) != null) {
            shipperListener.changeStatusOfShipper(shipper.getId());
        }
        mapOrderDeliveryForShipper.put(shipper.getId(), result);
    }

    private void preProcessMapFilterOrder(String market, List<Order> orders) {
        List<String> listStrings = mapFilterOrders.get(market);
        for (int i = 0; i < orders.size(); i++) {
            listStrings.remove(orders.get(i).getId());
        }

        if (listStrings.isEmpty()) {
            mapFilterOrders.put(market, null);
        }
    }

    private void sendNotificationOrderToShipper(String token, String compulsory) {
        Map<String, String> map = new HashMap<>();
        map.put("compulsory", compulsory);

        Firebase firebase = new Firebase();
        try {
            String result = firebase.pushNotifyOrdersToShipper(token, map);
            System.out.println("Firebase: " + result);
        } catch (FirebaseMessagingException | IOException | NullPointerException | IllegalArgumentException e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Send Notification: {0}", e.getMessage());
        }
    }

    @Override
    public void scanOrder() {
        synchronized (mapFilterShippers) {
            System.out.println("SCAN ORDER");
            ExecutorService executorService;

            for (Map.Entry<String, List<String>> entry : mapFilterShippers.entrySet()) {
                List<String> listIdShippers = entry.getValue();
                if (listIdShippers != null && !listIdShippers.isEmpty()) {
                    checkShipperIsAvailable(listIdShippers);
                    listIdShippers.sort(new SortByHighActive());

                    for (Iterator<String> it = listIdShippers.iterator(); it.hasNext();) {
                        Shipper shipper = ShipperController.mapAvailableShipper.get(it.next());
                        if (shipper != null) {
                            List<Order> listOrdersForShipper = checkConditionOrderIsHasRelease(mapFilterOrders.get(entry.getKey()), shipper);
                            if (listOrdersForShipper != null) {
                                executorService = Executors.newFixedThreadPool(3);
                                List<Future<?>> futures = new ArrayList<>();
                                futures.add(executorService.submit(new TimeGoingToMarket(entry.getKey(), shipper.getLat(), shipper.getLng())));
                                futures.add(executorService.submit(new TimeShopping(listOrdersForShipper)));
                                futures.add(executorService.submit(new TimeDelivery(listOrdersForShipper)));

                                int totalTime = 0;
                                try {
                                    for (Future<?> future : futures) {
                                        try {
                                            int result = (Integer) future.get();
                                            System.out.println("Time: " + result);
                                            totalTime += result;
                                        } catch (ExecutionException | InterruptedException e) {
                                            Logger.getLogger(CoreFunctions.class.getName()).log(Level.SEVERE, "Scan Order, Get total time - thread: {0}", e.getMessage());
                                        }
                                    }
                                } finally {
                                    executorService.shutdown();
                                }

                                totalTime = checkTimeReleaseOrderForShipper(totalTime);
                                DateTimeHelper helper = new DateTimeHelper();
                                int currentTime = helper.parseTimeToMinute(LocalTime.now(ZoneId.of("GMT+7")));
                                int onDeliveryTime = helper.parseTimeToMinute(listOrdersForShipper.get(0).getTimeDelivery().toLocalTime());

                                if (onDeliveryTime - currentTime <= totalTime) {
                                    preProcessOrderRelease(shipper, listOrdersForShipper);
                                    preProcessMapFilterOrder(entry.getKey(), listOrdersForShipper);
                                    sendNotificationOrderToShipper(shipper.getTokenFCM(), FALSE);

                                    System.out.println("Shipper: " + shipper.getId() + " is has Orders");
                                    listOrdersForShipper.forEach((order) -> {
                                        System.out.println(order);
                                    });
                                    System.out.println("");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private int test(String marketId, Shipper shipper, List<Order> listOrdersForShipper) {
        int total = 0;
        try {
            TimeGoingToMarket a = new TimeGoingToMarket(marketId, shipper.getLat(), shipper.getLng());
            int timeGoingToMarket = (Integer) a.call();

            total += timeGoingToMarket;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            TimeShopping b = new TimeShopping(listOrdersForShipper);
            int timeShopping = (Integer) b.call();

            total += timeShopping;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            TimeDelivery c = new TimeDelivery(listOrdersForShipper);
            int timeDelivery = (Integer) c.call();

            total += timeDelivery;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return total;
    }
}
