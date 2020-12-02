package com.smhu.core;

import com.google.firebase.messaging.FirebaseMessagingException;

import static com.smhu.controller.OrderController.mapCountOrderRelease;
import static com.smhu.controller.OrderController.mapOrderDeliveryForShipper;
import static com.smhu.controller.OrderController.mapOrderInQueue;
import static com.smhu.controller.OrderController.mapOrderIsWaitingAccept;
import static com.smhu.controller.OrderController.mapOrdersShipperReject;
import static com.smhu.controller.OrderController.mapOrderInProgress;
import static com.smhu.controller.ShipperController.mapAvailableShipper;
import static com.smhu.controller.ShipperController.mapInProgressShipper;
import static com.smhu.controller.ShipperController.mapShipperOrdersInProgress;

import com.smhu.account.Shipper;
import com.smhu.callable.TimeDelivery;
import com.smhu.callable.TimeGoingToMarket;
import com.smhu.callable.TimeShopping;
import com.smhu.comparator.SortByHighActive;
import com.smhu.comparator.SortByTimeDelivery;
import com.smhu.controller.MarketController;
import com.smhu.controller.OrderController;
import com.smhu.dao.OrderDAO;
import com.smhu.dao.ShipperDAO;
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
        this.shipperListener = new ShipperDAO();
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
//            System.out.println("SCAN SHIPPER");

            mapFilterShippers.entrySet().forEach((entry) -> {
                Market market = MarketController.mapMarket.get(entry.getKey());
                List<String> list = entry.getValue();
                for (Map.Entry<String, Shipper> shipper : mapAvailableShipper.entrySet()) {
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

    private List<Order> getOrdersOfShipper(Shipper shipper, List<String> listOrders) {
        List<Order> result = null;
        for (int i = 0; i < listOrders.size(); i++) {
            Order tmp = mapOrderInProgress.get(listOrders.get(i));
            if (tmp != null) {
                if (tmp.getShipper().equals(shipper.getId())) {
                    if (tmp.getStatus() < 23) {
                        if (result == null) {
                            result = new ArrayList();
                        }
                        result.add(tmp);
                    }
                }
            }
        }
        return result;
    }

    private boolean checkOrderInProgress(List<Order> listOrders, Shipper shipper, Order order) {
        order.setShipper(shipper.getId());
        order.setStatus(listOrders.get(0).getStatus());

        try {
            orderListener.updatetOrder(order);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Filter Order - Update DB: {0}", e.getMessage());
        }

        listOrders.add(order);
        mapOrderInProgress.put(order.getId(), order);

        try {
            TimeDelivery timeDelivery = new TimeDelivery(listOrders);
            timeDelivery.call();
        } catch (Exception e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Filter Order - Check In Progress: {0}", e.getMessage());
        }

        preProcessOrderBelongToShipper(shipper, order);
        preProcessOrderRelease(shipper, listOrders);
        return true;
    }

    private boolean checkOrderDeliveryForShipper(Shipper shipper, Order order) {
        List<OrderDelivery> listDelivery = mapOrderDeliveryForShipper.get(shipper.getId());

        if (listDelivery == null || listDelivery.isEmpty()) {
            return false;
        }

        order.setShipper(shipper.getId());

        List<Order> tmpList = new ArrayList();
        tmpList.add(order);
        for (OrderDelivery o : listDelivery) {
            for (Map.Entry<Order, Long> entry : mapOrderIsWaitingAccept.entrySet()) {
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

        preProcessOrderBelongToShipper(shipper, order);
        preProcessOrderRelease(shipper, tmpList);
        return true;
    }

    private boolean checkOrderShipperReject(Shipper shipper, Order order) {
        List<String> rejectedOrders = mapOrdersShipperReject.get(shipper.getId());
        if (rejectedOrders == null || rejectedOrders.isEmpty()) {
            return false;
        }

        if (rejectedOrders.contains(order.getId())) {
            return true;
        }
        return false;
    }

    private boolean filterOrderToShipperInProgress(Order order) {
        List<String> listIdShipper = mapFilterShippers.get(order.getMarket());
        if (listIdShipper != null) {
            List<String> listShipper = null;
            for (int i = listIdShipper.size() - 1; i >= 0; i--) {
                Shipper shipper = mapInProgressShipper.get(listIdShipper.get(i));
                if (shipper != null) {
                    if (listShipper == null) {
                        listShipper = new ArrayList();
                    }
                    listShipper.add(shipper.getId());
                }
            }

            if (listShipper == null) {
                return false;
            } else {
                if (listShipper.size() >= 2) {
                    listShipper.sort(new SortByHighActive());
                }

                for (String id : listShipper) {
                    boolean flag;
                    boolean isOutMaxOrder = false;
                    boolean isRejected = false;
                    Shipper shipper = mapInProgressShipper.get(id);
                    List<String> listIdOrders = mapShipperOrdersInProgress.get(shipper.getId());
//                    if (listOrders != null) {
//                        listOrders = checkOrderShipperReject(shipper, listOrders);
//                    }

                    if (listIdOrders.size() >= shipper.getMaxOrder()) {
                        isOutMaxOrder = true;
                    } else {
                        isRejected = checkOrderShipperReject(shipper, order);
                    }

                    if (!isOutMaxOrder && !isRejected) {
                        List<Order> listOrders = getOrdersOfShipper(shipper, listIdOrders);
                        if (listOrders != null) {
                            flag = checkOrderInProgress(listOrders, shipper, order);
                        } else {
                            flag = checkOrderDeliveryForShipper(shipper, order);
                        }

                        if (flag) {
                            int numRelease = mapCountOrderRelease.getOrDefault(order.getId(), 0);
                            mapCountOrderRelease.put(order.getId(), numRelease + 1);
                            sendNotificationOrderToShipper(shipper.getTokenFCM(), TRUE);
                            return true;
                        }
                    } // end if isOutMaxOrder
                } // end for
            } // end if listShipper
        } // end if listIdShipper
        return false;
    }

    @Override
    public void filterOrder(Order order) {
        boolean isHasShipperInProgress = filterOrderToShipperInProgress(order);

        if (isHasShipperInProgress) {
            return;
        }

        Market market = MarketController.mapMarket.get(order.getMarket());
        List<String> list = mapFilterOrders.get(market.getId());
        if (list == null) {
            list = new ArrayList<>();
            mapFilterOrders.put(market.getId(), list);
        }
        list.add(order.getId());
        mapOrderInQueue.put(order.getId(), order);
    }

    private int checkTimeReleaseOrderForShipper(int time) {
        return time >= 180 ? time + 30 : (180 - time) <= 30 ? time + 30 : 180;
    }

    private void checkShipperIsAvailable(List<String> listIdShippers) {
        for (int i = listIdShippers.size() - 1; i >= 0; i--) {
            if (!mapAvailableShipper.containsKey(listIdShippers.get(i))) {
                listIdShippers.remove(i);
            }
        }
    }

    private List<String> checkOrderShipperReject(Shipper shipper, List<String> list) {
        List<String> rejectedOrders = mapOrdersShipperReject.get(shipper.getId());
        if (rejectedOrders == null) {
            return list;
        }

        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                if (rejectedOrders.contains(list.get(i))) {
                    list.remove(i);
                }
            }

            if (list.isEmpty()) {
                list = null;
            }
        }

        return list;
    }

    private List<Order> checkConditionOrderIsHasRelease(List<String> listIdOrders, Shipper shipper) {
        listIdOrders = checkOrderShipperReject(shipper, listIdOrders);
        if (listIdOrders == null) {
            return null;
        }

        List<Order> listOrders = null;
        for (int i = listIdOrders.size() - 1; i >= 0; i--) {
            if (listIdOrders.get(i) != null) {
                if (listOrders == null) {
                    listOrders = new ArrayList();
                }
                listOrders.add(mapOrderInQueue.get(listIdOrders.get(i)));
            }
        }

        if (listOrders == null) {
            return null;
        }
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

    public void preProcessOrderBelongToShipper(Shipper shipper, Order order) {
        List<String> list = mapShipperOrdersInProgress.get(shipper.getId());
        if (list == null) {
            list = new ArrayList();
        }
        list.add(order.getId());
        mapShipperOrdersInProgress.put(shipper.getId(), list);
    }

    public void preProcessOrderBelongToShipper(Shipper shipper, List<Order> orders) {
        List<String> list = mapShipperOrdersInProgress.get(shipper.getId());
        if (list == null) {
            list = new ArrayList();
        }
        for (Order order : orders) {
            list.add(order.getId());
        }
        mapShipperOrdersInProgress.put(shipper.getId(), list);
    }

    private void preProcessOrderRelease(Shipper shipper, List<Order> orders) {
        List<OrderDelivery> result = new ArrayList();
        for (Order order : orders) {
            order.setShipper(shipper.getId());
            Order removedOrder = mapOrderInQueue.remove(order.getId());
            if (removedOrder != null) {
                mapOrderIsWaitingAccept.put(removedOrder, SystemTime.SYSTEM_TIME + (20 * 1000));
                int numRelease = mapCountOrderRelease.getOrDefault(removedOrder.getId(), 0);
                mapCountOrderRelease.put(removedOrder.getId(), numRelease + 1);
            } else {
                removedOrder = order;
            }

            result.add(new OrderDelivery(removedOrder));
        }

        if (mapAvailableShipper.containsKey(shipper.getId())) {
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

    @Override
    public void preProcessMapFilterOrder(String market, Order order) {
        List<String> listStrings = mapFilterOrders.get(market);
        if (listStrings == null) {
            return;
        }
        listStrings.remove(order.getId());

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
                    if (listIdShippers.size() >= 2) {
                        listIdShippers.sort(new SortByHighActive());
                    }

                    for (Iterator<String> it = listIdShippers.iterator(); it.hasNext();) {
                        Shipper shipper = mapAvailableShipper.get(it.next());
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
                                    preProcessOrderBelongToShipper(shipper, listOrdersForShipper);
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
