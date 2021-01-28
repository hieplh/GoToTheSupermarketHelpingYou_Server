package com.smhu.core;

import com.google.firebase.messaging.FirebaseMessagingException;

import static com.smhu.controller.OrderController.mapCountOrderRelease;
import static com.smhu.controller.OrderController.mapOrderDeliveryForShipper;
import static com.smhu.controller.OrderController.mapOrderInQueue;
import static com.smhu.controller.OrderController.mapOrderIsWaitingAccept;
import static com.smhu.controller.OrderController.mapOrdersShipperReject;
import static com.smhu.controller.OrderController.mapOrderInProgress;
import static com.smhu.controller.OrderController.mapOrderWaitingAfterShopping;
import static com.smhu.controller.OrderController.mapOrderIsCancel;

import static com.smhu.controller.ShipperController.mapAvailableShipper;
import static com.smhu.controller.ShipperController.mapInProgressShipper;
import static com.smhu.controller.ShipperController.mapShipperOrdersInProgress;

import com.smhu.account.Shipper;
import com.smhu.order.Order;
import com.smhu.market.Market;

import com.smhu.callable.TimeDelivery;
import com.smhu.callable.TimeGoingToMarket;
import com.smhu.callable.TimeShopping;

import com.smhu.comparator.SortByHighActive;
import com.smhu.comparator.SortByTimeDelivery;

import com.smhu.controller.MarketController;
import com.smhu.controller.OrderController;
import com.smhu.controller.TransactionController;
import com.smhu.dao.AccountDAO;

import com.smhu.dao.OrderDAO;
import com.smhu.dao.ShipperDAO;

import com.smhu.google.Firebase;
import com.smhu.google.matrixobj.ElementObject;
import com.smhu.google.matrixobj.MatrixObject;

import com.smhu.helper.DateTimeHelper;
import com.smhu.helper.ExtractElementDistanceMatrixApi;
import com.smhu.helper.ExtractRangeInMechanism;
import com.smhu.helper.MatrixObjBuilder;
import com.smhu.helper.SyncHelper;
import com.smhu.iface.IAccount;

import com.smhu.iface.ICore;
import com.smhu.iface.IOrder;
import com.smhu.iface.IShipper;
import com.smhu.iface.ITransaction;

import com.smhu.response.shipper.OrderDelivery;
import com.smhu.system.SystemTime;

import java.io.IOException;

import java.sql.SQLException;
import java.sql.Time;

import java.time.LocalTime;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Date;
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

    public static Map<String, Integer> AVG_TIME_TRAVEL_TO_MARKET = new HashMap<>();

    private final String DELIVERY_ORDER = "DELIVERY_ORDER";
    private final String PRE_DELIVERY_ORDER = "PRE_DELIVERY_ORDER";

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

                for (Map.Entry<String, Shipper> shipper : mapInProgressShipper.entrySet()) {
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
        }
    }

    private List<Order> getOrdersOfShipper(Shipper shipper, List<String> listOrders) {
        List<Order> result = null;
        for (int i = 0; i < listOrders.size(); i++) {
            Order tmp = mapOrderInProgress.get(listOrders.get(i));
            if (tmp != null) {
                if (tmp.getShipper().equals(shipper)) {
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
        order.setShipper(shipper);
        order.setStatus(listOrders.get(0).getStatus());
        order.setLastUpdate(new Time(new Date().getTime()));

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

        preProcessOrderBelongToShipper(shipper, listOrders);
        preProcessOrderRelease(shipper, listOrders, DELIVERY_ORDER);
        return true;
    }

    private boolean checkOrderReleaseToShipper(Shipper shipper, Order order) {
        List<OrderDelivery> listDelivery = mapOrderDeliveryForShipper.get(shipper.getUsername());

        if (listDelivery == null || listDelivery.isEmpty()) {
            return false;
        }

        order.setShipper(shipper);
        order.setLastUpdate(new Time(new Date().getTime()));

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

        mapOrderInQueue.put(order.getId(), order);
        preProcessOrderBelongToShipper(shipper, tmpList);
        preProcessOrderRelease(shipper, tmpList, DELIVERY_ORDER);
        return true;
    }

    private boolean checkOrderShipperReject(Shipper shipper, Order order) {
        List<String> rejectedOrders = mapOrdersShipperReject.get(shipper.getUsername());
        if (rejectedOrders == null || rejectedOrders.isEmpty()) {
            return false;
        }

        return rejectedOrders.contains(order.getId());
    }

    private boolean filterOrderToShipperInProgress(Order order) {
        List<String> listIdShipper = mapFilterShippers.get(order.getMarket().getId());
        if (listIdShipper != null) {
            List<String> listIdShippers = null;
            for (int i = listIdShipper.size() - 1; i >= 0; i--) {
                Shipper shipper = mapInProgressShipper.get(listIdShipper.get(i));
                if (shipper != null) {
                    if (listIdShippers == null) {
                        listIdShippers = new ArrayList();
                    }
                    listIdShippers.add(shipper.getUsername());
                }
            }

            if (listIdShippers == null) {
                return false;
            } else {
                List<Shipper> listShippers = getListShipperFromId(listIdShippers);
                for (Shipper shipper : listShippers) {
                    boolean flag;
                    boolean isOutMaxOrder = false;
                    boolean isRejected = false;
                    List<String> listIdOrders = mapShipperOrdersInProgress.get(shipper.getUsername());

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
                            flag = checkOrderReleaseToShipper(shipper, order);
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
        boolean isHasShipperInProgress;
        try {
            isHasShipperInProgress = filterOrderToShipperInProgress(order);
        } catch (Exception e) {
            isHasShipperInProgress = false;
        }

        if (isHasShipperInProgress) {
            return;
        }

        Market market = order.getMarket();
        List<String> list = mapFilterOrders.get(market.getId());
        if (list == null) {
            list = new ArrayList<>();
            mapFilterOrders.put(market.getId(), list);
        }
        list.add(order.getId());
        mapOrderInQueue.put(order.getId(), order);
    }

    public void scanOrderExpire() {
        synchronized (mapOrderInQueue) {
            DateTimeHelper dateTime = new DateTimeHelper();
            ITransaction transactionListener = new TransactionController().getTransactionListener();
            IAccount accountListener = new AccountDAO();
            OrderDAO dao = new OrderDAO();
            Time curTime;
            List<Order> result = null;
            for (Map.Entry<String, Order> entry : mapOrderInQueue.entrySet()) {
                Order order = entry.getValue();
                int timeDelivery = dateTime.parseTimeToMinute(order.getTimeDelivery().toLocalTime());
                curTime = new Time(SystemTime.SYSTEM_TIME);
                int currentSystemTime = dateTime.parseTimeToMinute(curTime.toLocalTime());

                if (currentSystemTime - timeDelivery >= (-90)) {
                    order.setStatus(-13);
                    order.setRefundCost(order.getTotalCost());
                    order.setShipper(null);
                    order.setLastUpdate(new Time(new Date().getTime()));

                    try {
                        transactionListener.updateRefundTransaction(order.getCust(), order.getCust(), order.getTotalCost(), order.getStatus(), order.getId());
                        accountListener.updateWalletAccount(order.getCust(), order.getTotalCost());
                        accountListener.updateNumCancel(order.getCust(), 1);
                        dao.CancelOrder(order);
                    } catch (ClassNotFoundException | SQLException e) {
                        Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "OrderController - Cancel Cust: {0}", e.getMessage());
                    }

                    if (result == null) {
                        result = new ArrayList();
                    }
                    result.add(order);
                    mapOrderIsCancel.put(order.getId(), order);
                    preProcessMapFilterOrder(order.getMarket().getId(), order);
                }
            }

            if (result != null) {
                for (Order order : result) {
                    mapOrderInQueue.remove(order.getId());
                }
            }
        }
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
        List<String> rejectedOrders = mapOrdersShipperReject.get(shipper.getUsername());
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
        Time curTime = new Time(SystemTime.SYSTEM_TIME);
        for (Order order : listOrders) {
            if (count >= shipper.getMaxOrder()) {
                break;
            }
            releaseTime = order.getTimeDelivery().toLocalTime();
            if (helper.parseTimeToMinute(releaseTime) - helper.parseTimeToMinute(curTime.toLocalTime()) <= (4 * 60) + (count * 30)) {
                if (listOrderResult == null) {
                    listOrderResult = new ArrayList();
                }
                listOrderResult.add(order);
                count++;
            }
        }
        return listOrderResult;
    }

    public void preProcessOrderBelongToShipper(Shipper shipper, List<Order> orders) {
        List<String> list = new ArrayList();
        for (Order order : orders) {
            list.add(order.getId());
        }
        mapShipperOrdersInProgress.put(shipper.getUsername(), list);
    }

    private void preProcessOrderRelease(Shipper shipper, List<Order> orders, String type) {
        SyncHelper sync = new SyncHelper();
        List<OrderDelivery> result = new ArrayList();
        for (Order order : orders) {
            order.setShipper(shipper);
            Order removedOrder = mapOrderInQueue.remove(order.getId());
            if (removedOrder != null) {
                mapOrderIsWaitingAccept.put(removedOrder, SystemTime.SYSTEM_TIME + (20 * 1000));
                int numRelease = mapCountOrderRelease.getOrDefault(removedOrder.getId(), 0);
                mapCountOrderRelease.put(removedOrder.getId(), numRelease + 1);
            } else {
                removedOrder = order;
            }
            removedOrder.setCostDelivery(removedOrder.getCostDelivery() * ((100 - removedOrder.getCommissionShipping()) / 100.0));
            removedOrder.setCostShopping(removedOrder.getCostShopping() * ((100 - removedOrder.getCommissionShopping()) / 100.0));

            recordReleasedOrder(removedOrder, shipper);
            result.add(sync.syncOrderSystemToOrderDelivery(removedOrder));
        }

        if (mapAvailableShipper.containsKey(shipper.getUsername())) {
            shipperListener.changeStatusOfShipper(shipper.getUsername());
        }

        switch (type) {
            case DELIVERY_ORDER:
                mapOrderDeliveryForShipper.put(shipper.getUsername(), result);
                break;
            case PRE_DELIVERY_ORDER:
                mapOrderWaitingAfterShopping.put(shipper.getUsername(), new ArrayList(orders));
                break;
            default:
                break;
        }
    }

    private void preProcessMapFilterOrder(String market, List<Order> orders) {
        List<String> listStrings = mapFilterOrders.get(market);
        Iterator<Order> iterator = orders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            listStrings.remove(order.getId());
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

    private void recordReleasedOrder(Order order, Shipper shipper) {
        try {
            orderListener.insertRecordOrder(order, shipper);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Record Released Order: {0}", e.getMessage());
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

    private void scanOrderForShipperAvailable(Shipper shipper, List<String> orders, String marketId) {
        ExecutorService executorService;
        List<Order> listOrdersForShipper = checkConditionOrderIsHasRelease(orders, shipper);
        if (listOrdersForShipper != null) {
            executorService = Executors.newFixedThreadPool(3);
            List<Future<?>> futures = new ArrayList<>();
            futures.add(executorService.submit(new TimeGoingToMarket(marketId, shipper.getLat(), shipper.getLng())));
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
                preProcessOrderRelease(shipper, listOrdersForShipper, DELIVERY_ORDER);
                preProcessMapFilterOrder(marketId, listOrdersForShipper);
                preProcessOrderBelongToShipper(shipper, listOrdersForShipper);
                sendNotificationOrderToShipper(shipper.getTokenFCM(), FALSE);

                System.out.println("Shipper: " + shipper.getUsername() + " is has Orders");
                listOrdersForShipper.forEach((order) -> {
                    System.out.println(order);
                });
                System.out.println("");
            }
        }
    }

    private void scanOrderForShipperInProgress(Shipper shipper, String marketId,
            List<String> listOrderBelongToShipper, List<String> listIdOrders) {
        if (checkShipperIsInDeliveryProgress(shipper)) {
            List<Order> result = null;
            int count = 0;
            if (listIdOrders != null) {
                List<Order> listOrders = checkConditionOrderIsHasRelease(listIdOrders, shipper);
                if (listOrders.size() > 1) {
                    listOrders.sort(new SortByTimeDelivery());
                }

                for (Iterator<Order> o = listOrders.iterator(); o.hasNext();) {
                    Order order = o.next();
                    try {
                        MatrixObject matrixObject = MatrixObjBuilder.getMatrixObject(new String[]{shipper.getLat(), shipper.getLng()},
                                getListPhysicalAddresses(listOrderBelongToShipper));
                        ExtractElementDistanceMatrixApi extract = new ExtractElementDistanceMatrixApi();
                        List<String> distances = extract.getListDistance(extract.getListElements(matrixObject), "value");
                        int distance = 9999;
                        for (String d : distances) {
                            distance = Integer.parseInt(d);
                        }

                        if (distance <= 2000) {
                            if (count <= shipper.getMaxOrder()) {
//                                try {
//                                if (checkDistanceBetweenNewAndOldOrders(order.getAddressDelivery(), ListOrdersBelongToShipper)) {
//
//                                }
                                if (result == null) {
                                    result = new ArrayList();
                                }
                                result.add(order);
                                count++;
//                                } catch (IOException e) {
//                                    Logger.getLogger(CoreFunctions.class.getName()).log(Level.SEVERE, "Scan Order - 23: {0}", e.getMessage());
//                                }
                            }
                        } else {
                            break;
                        }
                    } catch (IOException e) {
                        Logger.getLogger(CoreFunctions.class.getName()).log(Level.SEVERE, "Scan Order - 23 - Get Distance Shipper and Order: {0}", e.getMessage());
                    }
                } // end for

                if (result != null) {
                    TimeDelivery timeDelivery = new TimeDelivery(result);
                    try {
                        timeDelivery.call();

                        preProcessOrderRelease(shipper, result, PRE_DELIVERY_ORDER);
                        preProcessMapFilterOrder(marketId, result);
                        sendNotificationOrderToShipper(shipper.getTokenFCM(), TRUE);

                        System.out.println("Map Shipper Order Belong To Shipper");
                        if (listOrderBelongToShipper != null) {
                            for (Order listOrder : listOrders) {
                                System.out.println(listOrder);
                            }
                        }
                    } catch (Exception e) {
                        Logger.getLogger(CoreFunctions.class.getName()).log(Level.SEVERE, "Swap Order Ascending Moving - 23: {0}", e.getMessage());
                    }
                }
            } // end if list order of market
        } // end if shipper in delivery
    }

    @Override
    public void scanOrder() {
        scanOrderExpire();

        synchronized (mapFilterShippers) {
            System.out.println("SCAN ORDER");

            for (Map.Entry<String, List<String>> entry : mapFilterShippers.entrySet()) {
                List<String> listIdShippers = entry.getValue();
                if (listIdShippers != null && !listIdShippers.isEmpty()) {
                    List<Shipper> listShippers = getListShipperFromId(listIdShippers);
                    for (Iterator<Shipper> it = listShippers.iterator(); it.hasNext();) {
                        Shipper shipper = it.next();
                        if (mapAvailableShipper.containsKey(shipper.getUsername())) {
                            scanOrderForShipperAvailable(shipper, mapFilterOrders.get(entry.getKey()), entry.getKey());
                        } else {
                            List<Order> listPreOrdersOfShipper = mapOrderWaitingAfterShopping.get(shipper.getUsername());
                            if (listPreOrdersOfShipper == null) {
                                scanOrderForShipperInProgress(shipper, entry.getKey(),
                                        mapShipperOrdersInProgress.get(shipper.getUsername()), mapFilterOrders.get(entry.getKey()));
                            }
                        } // end if shipper
                    } // end for
                }
            }
        }
    }

    private boolean checkShipperIsInDeliveryProgress(Shipper shipper) {
        List<String> listOrder = mapShipperOrdersInProgress.get(shipper.getUsername());
        if (listOrder == null) {
            return false;
        }

        if (listOrder.isEmpty() || listOrder.size() > 1) {
            return false;
        }

        Order order = mapOrderInProgress.get(listOrder.get(0));
        if (order == null) {
            return false;
        }

        if (order.getStatus() != 23) {
            return false;
        }

        return true;
    }

    private List<String> getListPhysicalAddresses(List<String> listOrders) {
        List<String> list = new ArrayList();
        listOrders.forEach((s) -> {
            list.add(mapOrderInProgress.get(s).getAddressDelivery());
        });
        return list;
    }

    private List<Order> getListOrderInDeliveryProgress(List<Order> newOrders, List<String> oldOrders) {
        oldOrders.forEach((oldOrder) -> {
            newOrders.add(mapOrderInProgress.get(oldOrder));
        });
        return newOrders;
    }

    private boolean checkDistanceBetweenNewAndOldOrders(String newAddress, List<String> oldOrders) throws IOException {
        MatrixObject matrixObject = MatrixObjBuilder.getMatrixObject(newAddress,
                getListPhysicalAddresses(oldOrders));

        ExtractElementDistanceMatrixApi extract = new ExtractElementDistanceMatrixApi();
        List<ElementObject> elements = extract.getListElements(matrixObject);
        List<String> distances = extract.getListDistance(elements, "value");
        return distances.stream().anyMatch((distance) -> (Integer.parseInt(distance) <= 3000));
    }

    private void updateOrderAfterInDelivery(Shipper shipper, List<Order> newOrders) {
        List<Order> tmpList = new ArrayList(newOrders);
        mapOrderWaitingAfterShopping.put(shipper.getUsername(), tmpList);

//        List<String> list = mapShipperOrdersInProgress.get(shipper.getUsername());
//        Order tmp = mapOrderInProgress.get(list.get(0));
//        for (Order order : newOrders) {
//            order.setShipper(shipper.getUsername());
//            order.setStatus(tmp.getStatus() - 1);
//            order.setLastUpdate(new Time(new Date().getTime()));
//            mapOrderInProgress.put(order.getId(), order);
//
//            try {
//                orderListener.updatetOrder(order);
//            } catch (ClassNotFoundException | SQLException e) {
//                Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Scan Order - 23 - Update DB: {0}", e.getMessage());
//            }
//        }
    }

    private List<Shipper> getListShipperFromId(List<String> listIdShippers) {
        List<Shipper> listShippers = new ArrayList();
        for (String id : listIdShippers) {
            Shipper s = shipperListener.getShipper(id);
            if (s != null) {
                listShippers.add(s);
            }

        }

        if (listShippers.size() >= 2) {
            listShippers.sort(new SortByHighActive());
        }
        return listShippers;
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
