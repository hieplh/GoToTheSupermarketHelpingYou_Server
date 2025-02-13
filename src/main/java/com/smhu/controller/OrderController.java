package com.smhu.controller;

import com.google.firebase.messaging.FirebaseMessagingException;

import static com.smhu.controller.ShipperController.mapMechanismReleaseOrder;
import static com.smhu.controller.ShipperController.mapAvailableShipper;
import static com.smhu.controller.ShipperController.mapInProgressShipper;
import static com.smhu.controller.ShipperController.mapShipperOrdersInProgress;

import static com.smhu.controller.CommissionController.commission;

import com.smhu.GototheSupermarketHelpingYouApplication;
import com.smhu.account.Account;
import com.smhu.account.Customer;
import com.smhu.account.Shipper;

import com.smhu.core.CoreFunctions;

import com.smhu.dao.AccountDAO;
import com.smhu.dao.OrderDAO;
import com.smhu.dao.ShipperDAO;

import com.smhu.google.Firebase;
import com.smhu.google.matrixobj.MatrixObject;
import com.smhu.helper.GsonHelper;
import com.smhu.helper.SyncHelper;

import com.smhu.iface.IAccount;
import com.smhu.iface.ICore;
import com.smhu.iface.IMain;
import com.smhu.iface.IOrder;
import com.smhu.iface.IShipper;
import com.smhu.iface.IStatus;
import com.smhu.iface.ITransaction;

import com.smhu.order.Order;
import com.smhu.order.OrderDetail;

import com.smhu.request.customer.OrderRequestCustomer;
import com.smhu.response.ResponseMsg;
import com.smhu.response.customer.OrderResponseCustomer;
import com.smhu.response.moderator.OrderOverall;
import com.smhu.response.shipper.OrderDelivery;
import com.smhu.response.shipper.OrderDoneDelivery;
import com.smhu.response.shipper.OrderShipper;

import com.smhu.url.UrlConnection;

import java.io.IOException;
import java.io.InputStreamReader;

import java.sql.SQLException;
import java.sql.Time;

import java.time.LocalTime;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class OrderController {

    public final static Map<String, Order> mapOrderInQueue = new HashMap<>();

    public final static Map<Order, Long> mapOrderIsWaitingAccept = new HashMap<>();
    public final static Map<String, List<OrderDelivery>> mapOrderDeliveryForShipper = new HashMap<>();

    public final static Map<String, Integer> mapCountOrderRelease = new HashMap<>();

    public final static Map<String, Order> mapOrderInProgress = new HashMap<>();
    public final static Map<String, List<Order>> mapOrderWaitingAfterShopping = new HashMap<>();
    public final static Map<String, Order> mapOrderIsDone = new HashMap<>();

    public final static Map<String, List<String>> mapOrdersShipperReject = new HashMap<>();
    public final static Map<String, Order> mapOrderIsCancelInQueue = new HashMap<>();
    public final static Map<String, Order> mapOrderIsCancel = new HashMap<>();

    private final OrderService service;

    private final IOrder dao;
    private final IShipper shipperListener;
    private final IStatus statusListener;
    private final IMain mainListener;
    private final IAccount accountListener;
    private final ITransaction transactionListener;
    private final ICore coreListener;

    private final String CUSTOMER = "CUSTOMER";
    private final String STAFF = "STAFF";
    private final String SHIPPER = "SHIPPER";

    public OrderController() {
        service = new OrderService();

        statusListener = new StatusController().getStatusListener();
        mainListener = new GototheSupermarketHelpingYouApplication().getMainListener();
        shipperListener = new ShipperDAO();
        transactionListener = new TransactionController().getTransactionListener();
        coreListener = new CoreFunctions();

        dao = new OrderDAO();
        accountListener = new AccountDAO();
    }

    @GetMapping("order/{type}/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable("id") String id, @PathVariable("type") String type) {
        try {
            Order order = dao.getOrderById(id, type);
            if (order == null) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
            mainListener.loadOrderDetail(order);
            switch (type.toUpperCase()) {
                case CUSTOMER:
                    return new ResponseEntity<>(order, HttpStatus.OK);
                case STAFF:
                    OrderOverall orderStaff = new OrderOverall(order);
                    orderStaff.setMarket(order.getMarket());
                    orderStaff.setCustomer((Customer) accountListener.getAccountById(order.getCust(), CUSTOMER));

                    Shipper shipper = null;
                    if (order.getShipper() != null) {
                        shipper = shipperListener.getShipper(order.getShipper().getUsername());
                        if (shipper == null) {
                            shipper = (Shipper) accountListener.getAccountById(order.getShipper().getUsername(), SHIPPER);
                        }
                    }

                    orderStaff.setShipper(shipper);
                    if (order.getAuthor() != null) {
                        orderStaff.setAuthor((Account) accountListener.getAccountById(order.getAuthor(), STAFF));
                    }
//                    orderStaff.setAlters(dao.getListShipperAlter(id));
                    return new ResponseEntity<>(orderStaff, HttpStatus.OK);
                default:
                    return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/orders/{customerId}")
    public ResponseEntity getOrdersIsPurchase(@PathVariable("customerId") String customerId) {
        List<OrderResponseCustomer> listResponseOrders = null;
        List<Order> listTmp = null;

        for (Order order : mapOrderInQueue.values()) {
            if (order.getCust().equals(customerId)) {
                if (listTmp == null) {
                    listTmp = new ArrayList<>();
                }
                listTmp.add(order);
            }
        }
        for (Order order : mapOrderInProgress.values()) {
            if (order.getCust().equals(customerId)) {
                if (listTmp == null) {
                    listTmp = new ArrayList<>();
                }
                listTmp.add(order);
            }
        }
        if (listTmp == null) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        Collections.sort(listTmp);

        SyncHelper sync = new SyncHelper();
        for (Order order : listTmp) {
            if (listResponseOrders == null) {
                listResponseOrders = new ArrayList<>();
            }
            listResponseOrders.add(sync.syncOrderSystemToOrderResponseCustomer(order));
        }
        return new ResponseEntity<>(listResponseOrders, HttpStatus.OK);
    }

    @GetMapping("orders/all")
    public ResponseEntity<?> getAllOrders() {
        List<Order> list = new ArrayList<>();
        list.addAll(mapOrderInQueue.values());
        list.addAll(mapOrderIsWaitingAccept.keySet().stream().collect(Collectors.toList()));
        list.addAll(mapOrderInProgress.values());
        list.addAll(mapOrderIsDone.values());
        list.addAll(mapOrderIsCancel.values());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/order")
    public ResponseEntity<?> newOrder(@RequestBody OrderRequestCustomer orderReceive) {
        String result;
        try {
            int status = statusListener.getStatusIsPaidOrder();
            java.sql.Date date = new java.sql.Date(new Date().getTime());
            Time time = new Time(new Date().getTime());

            Order order = new Order();
            order.setId(new OrderService().generateId(orderReceive));
            order.setCust(orderReceive.getCust());
            order.setAddressDelivery(orderReceive.getAddressDelivery());
            order.setNote(orderReceive.getNote());
            order.setMarket(MarketController.mapMarket.get(orderReceive.getMarket()));
            order.setCreateDate(date);
            order.setCreateTime(time);
            order.setLastUpdate(time);
            order.setStatus(status);
            order.setCostShopping(orderReceive.getCostShopping());
            order.setCostDelivery(orderReceive.getCostDelivery());
            order.setTotalCost(orderReceive.getTotalCost());
            order.setDateDelivery(orderReceive.getDateDelivery());
            order.setTimeDelivery(orderReceive.getTimeDelivery());
            order.setDetails(orderReceive.getDetails());
            order.setCommissionShipping(commission.getCommissionShipping());
            order.setCommissionShopping(commission.getCommissionShopping());

            result = dao.insertOrder(order);

            if (result == null) {
                return new ResponseEntity<>(new ResponseMsg("Insert new order failed"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (dao.insertOrderDetail(result, order.getDetails()) == null) {
                return new ResponseEntity<>(new ResponseMsg("Insert new order detail failed"), HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                int count = 0;
                for (OrderDetail detail : order.getDetails()) {
                    detail.setId(result + String.valueOf(++count));
                }
            }
            transactionListener.updateDeliveryTransaction(order.getCust(), order.getCust(),
                    order.getTotalCost() * -1, status, order.getId());
            accountListener.updateWalletAccount(order.getCust(), order.getTotalCost() * -1);

            service.addOrderInqueue(order);
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ResponseMsg(result), HttpStatus.OK);
    }

    @PutMapping("/orders/update")
    public ResponseEntity<?> updatetOrder(@RequestBody List<OrderShipper> listOrders) {
        SyncHelper sync = new SyncHelper();

        List<OrderShipper> listResults = null;
        List<OrderDoneDelivery> listVerificationResults = null;
        Shipper shipper = service.getShipper(listOrders.get(0));

        int status;
        int numCancel = 0;
        boolean orderIsDone = false;

        for (OrderShipper orderInProcess : listOrders) {
            if (mapOrderIsCancel.containsKey(orderInProcess.getId())) {
                numCancel++;
            } else {
                boolean isHasOrderCancel = false;
                Order order = service.checkOrderIsInProgress(orderInProcess);
                if (order == null) {
                    Order orderWaitAccept;
                    synchronized (mapOrderIsWaitingAccept) {
                        orderWaitAccept = mapOrderIsWaitingAccept.keySet()
                                .stream()
                                .filter(o -> orderInProcess.getId().equals(o.getId()))
                                .findFirst()
                                .orElse(null);
                    }

                    if (orderWaitAccept == null) {
                        numCancel++;
                        isHasOrderCancel = true;
                    } else {
                        if (!orderWaitAccept.getShipper().getUsername().equals(orderInProcess.getShipper())) {
                            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
                        }

                        mapOrderIsWaitingAccept.remove(orderWaitAccept);
                        mapCountOrderRelease.remove(orderWaitAccept.getId());
                        service.removeOrderDeliveryForShipper(shipper, orderWaitAccept);
                        service.removePreOrderOfShipper(shipper, orderWaitAccept);

                        order = orderWaitAccept;
                        mapOrderInProgress.put(order.getId(), order);
                    }
                } // end if order == null

                if (isHasOrderCancel) {
                    if (mapShipperOrdersInProgress.get(orderInProcess.getShipper()) == null) {
                        return new ResponseEntity<>(new ResponseMsg("Đơn hàng của bạn đã hoàn thành hoặc bị hủy"), HttpStatus.NOT_FOUND);
                    }
                } else {
                    if (!String.valueOf(order.getStatus()).matches("2\\d")) {
                        status = statusListener.getStatusIsAccept();
                    } else {
                        status = order.getStatus() + 1;
                    }

                    int statusIsDone = statusListener.getStatusIsDoneOrder();
                    service.updateOrder(shipper, order, status);

                    if (status == statusIsDone) {
                        try {
                            orderIsDone = true;
                            if (listVerificationResults == null) {
                                listVerificationResults = new ArrayList();
                            }
                            OrderDoneDelivery result = service.executeOrderIsDone(shipper, order, status, sync);
                            listVerificationResults.add(result);
                        } catch (ClassNotFoundException | SQLException e) {
                            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Update Order - Done: {0}", e.getMessage());
                            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        if (listResults == null) {
                            listResults = new ArrayList<>();
                        }
                        listResults.add(sync.syncOrderSystemToOrderShipper(order));
                    }
                } // end if check boolean isHasOrderIsCancel
            } // end if mapOrderIsCancel
        } // end for

        if (numCancel == listOrders.size()) {
            service.checkShipperOutOfProgress();
            return new ResponseEntity("Đơn hàng của bạn đã bị hủy bởi khách hàng", HttpStatus.METHOD_NOT_ALLOWED);
        }

        if (orderIsDone) {
            if (mapShipperOrdersInProgress.get(shipper.getUsername()).isEmpty()) {
                mapOrderDeliveryForShipper.remove(shipper.getUsername());
                mapShipperOrdersInProgress.remove(shipper.getUsername());
                shipperListener.changeStatusOfShipper(shipper.getUsername());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(listVerificationResults, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(listResults, HttpStatus.OK);
    }

//    @GetMapping("switch/{orderId}/{authorId}")
//    public ResponseEntity<?> changeShipper(@PathVariable("orderId") String orderId, @PathVariable("authorId") String authorId) {
//        MatrixObject distanceObj;
//        List<Shipper> listShipper = service.getShippers(new SortByLowActive());
//        if (listShipper.isEmpty()) {
//            return new ResponseEntity<>(null, HttpStatus.OK);
//        }
//
//        synchronized (listShipper) {
//            Order order = mapOrderInProgress.get(orderId);
//            Shipper oldShipper = shipperListener.getShipper(order.getShipper().getUsername());
//            for (Iterator<Shipper> it = listShipper.iterator(); it.hasNext();) {
//                Shipper newShipper = it.next();
//                if (newShipper.getLat() != null && newShipper.getLng() != null) {
//                    try {
//                        String[] sourceLocation = new String[]{oldShipper.getLat(), oldShipper.getLng()};
//                        String[] destinationLocation = new String[]{newShipper.getLat(), newShipper.getLng()};
//
//                        distanceObj = service.getDistanceMatrixObject(sourceLocation, destinationLocation);
//                        ExtractElementDistanceMatrixApi extract = new ExtractElementDistanceMatrixApi();
//                        List<ElementObject> listElments = extract.getListElements(distanceObj);
//                        List<String> listDistanceValue = extract.getListDistance(listElments, "value");
//
//                        int range = service.getTheLongestDistanceInMechanism();
//                        for (String distanceString : listDistanceValue) {
//                            int distance = Integer.parseInt(distanceString);
//                            if (distance <= range) {
//                                order.setShipper(newShipper.getUsername());
//                                order.setLastUpdate(new Time(new Date().getTime()));
//                                dao.updatetOrder(order);
//                                dao.insertAlterShipper(orderId, oldShipper.getUsername(), newShipper.getUsername(), authorId);
//                                it.remove();
//
//                                // wait process new shipper meet old shipper
//                                shipperListener.changeStatusOfShipper(oldShipper.getUsername());
//                                shipperListener.changeStatusOfShipper(newShipper.getUsername());
//                                System.out.println("Change shipper from: " + oldShipper.getUsername() + " to: " + newShipper.getUsername());
//                                return new ResponseEntity<>(order, HttpStatus.OK);
//                            }
//                        }
//                    } catch (IOException | ClassNotFoundException | NumberFormatException | SQLException e) {
//                        Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Scan Order: {0}", e.getMessage());
//                    }
//                }
//            }
//        }
//        return new ResponseEntity<>(null, HttpStatus.OK);
//    }
    @GetMapping("/tracking/{orderId}")
    public ResponseEntity trackingOrder(@PathVariable("orderId") String orderId) {
        Order order = mapOrderInProgress.get(orderId);
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
        }
        Shipper shipper = shipperListener.getShipper(order.getShipper().getUsername());

        Map<String, String> map = new HashMap();
        map.put("lat", shipper.getLat());
        map.put("lng", shipper.getLng());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("delete/{orderId}/{type}/{personId}")
    public ResponseEntity<?> cancelOrder(@PathVariable("orderId") String orderId, @PathVariable("type") String type,
            @PathVariable("personId") String accountId) {
        Order order;
        Shipper shipper;
        int status;
        double chargeCost;

        switch (type.toUpperCase()) {
            case STAFF:
                if (!mapOrderInProgress.containsKey(orderId)) {
                    return new ResponseEntity<>("Your cancel request, order_id: " + orderId
                            + ", temporary only customer can cancel", HttpStatus.METHOD_NOT_ALLOWED);
                }

                order = mapOrderInProgress.remove(orderId);
                if (order == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND.toString(), HttpStatus.NOT_FOUND);
                }
                mapOrderIsCancel.put(order.getId(), order);

                status = order.getStatus() * -1;
                order.setStatus(status);
                order.setLastUpdate(new Time(new Date().getTime()));
                order.setAuthor(accountId);
                order.setRefundCost(order.getTotalCost());
                //mapOrderIsCancelInQueue.put(order.getId(), order);

                chargeCost = order.getCostShopping() * -1;

                shipper = shipperListener.getShipper(order.getShipper().getUsername());
                shipper.setNumCancel(shipper.getNumCancel() + 1);
                shipper.setWallet(shipper.getWallet() + chargeCost);
                try {
                    service.cancelMapShipperOrder(orderId, shipper.getUsername());
                    shipperListener.updateNumCancel(shipper.getUsername(), 1);

                    transactionListener.updateDeliveryTransaction(shipper.getUsername(), accountId, chargeCost, status, order.getId());
                    transactionListener.updateRefundTransaction(order.getCust(), accountId, order.getTotalCost(), status, orderId);

                    accountListener.updateWalletAccount(order.getShipper().getUsername(), chargeCost);
                    accountListener.updateWalletAccount(order.getCust(), order.getTotalCost());

                    if (mapShipperOrdersInProgress.get(accountId) == null) {
                        shipperListener.changeStatusOfShipper(shipper.getUsername());
                    }
                    shipperListener.checkWalletShipperAccount(shipper);
                    dao.CancelOrder(order);

                    service.sendNotificationOrderToShipper(shipper.getTokenFCM(), orderId);
                } catch (SQLException | ClassNotFoundException e) {
                    Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
                }
//                return new ResponseEntity<>(new ResponseMsg("Your cancel request, order_id: " + order.getId()
//                        + ", is processing"), HttpStatus.OK);
                return new ResponseEntity<>(new ResponseMsg("Your cancel request, order_id: " + order.getId()
                        + ", is canceled"), HttpStatus.OK);
            case SHIPPER:
                order = mapOrderInProgress.remove(orderId);
                if (order == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND.toString(), HttpStatus.NOT_FOUND);
                }

                status = order.getStatus() * -1;
                order.setStatus(status);
                order.setLastUpdate(new Time(new Date().getTime()));
                order.setAuthor(accountId);
                order.setRefundCost(order.getTotalCost());
                //mapOrderIsCancelInQueue.put(order.getId(), order);

                chargeCost = order.getCostDelivery() * -1;

                shipper = shipperListener.getShipper(accountId);
                shipper.setNumCancel(shipper.getNumCancel() + 1);
                shipper.setWallet(shipper.getWallet() + chargeCost);
                try {
                    service.cancelMapShipperOrder(orderId, accountId);
                    shipperListener.updateNumCancel(accountId, 1);

                    transactionListener.updateDeliveryTransaction(shipper.getUsername(), shipper.getUsername(), chargeCost, status, order.getId());
                    transactionListener.updateRefundTransaction(order.getCust(), shipper.getUsername(), order.getTotalCost(), status, orderId);

                    accountListener.updateWalletAccount(shipper.getUsername(), chargeCost);
                    accountListener.updateWalletAccount(order.getCust(), order.getTotalCost());

                    if (mapShipperOrdersInProgress.get(accountId) == null) {
                        shipperListener.changeStatusOfShipper(shipper.getUsername());
                    }
                    shipperListener.checkWalletShipperAccount(shipper);
                    dao.CancelOrder(order);
                    mapOrderIsCancel.put(order.getId(), order);
                } catch (SQLException | ClassNotFoundException e) {
                    Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
                }
//                return new ResponseEntity<>(new ResponseMsg("Your cancel request, order_id: " + order.getId()
//                        + ", is processing"), HttpStatus.OK);
                return new ResponseEntity<>(new ResponseMsg("Your cancel request, order_id: " + order.getId()
                        + ", is canceled"), HttpStatus.OK);
            case CUSTOMER:
                if (mapOrderInProgress.containsKey(orderId)) {
                    order = mapOrderInProgress.get(orderId);
                    if (order.getCust().equals(accountId)) {
                        return new ResponseEntity<>("Đơn hàng không phải của bạn", HttpStatus.METHOD_NOT_ALLOWED);
                    }
                    return new ResponseEntity<>("Người giao hàng đã nhận đơn.\n"
                            + "Xin vui lòng đợi tới khi đơn hàng hoàn thành", HttpStatus.METHOD_NOT_ALLOWED);
                }

                order = mapOrderInQueue.getOrDefault(orderId, null);
                if (order == null) {
                    synchronized (mapOrderIsWaitingAccept) {
                        order = mapOrderIsWaitingAccept.keySet()
                                .stream()
                                .filter(o -> orderId.equals(o.getId()))
                                .filter(o -> accountId.equals(o.getCust()))
                                .findFirst()
                                .orElse(null);
                    }

                    if (order == null) {
                        return new ResponseEntity<>("Đơn hàng không phải của bạn", HttpStatus.METHOD_NOT_ALLOWED);
                    }
                }

                if (!order.getCust().equals(accountId)) {
                    return new ResponseEntity<>("Đơn hàng không phải của bạn", HttpStatus.METHOD_NOT_ALLOWED);
                }

                mapCountOrderRelease.remove(orderId);
                service.cancelMapShipperOrder(orderId);
                mapOrderIsCancel.put(order.getId(), order);
                coreListener.preProcessMapFilterOrder(order.getMarket().getId(), order);
                if (order.getShipper() != null) {
                    mapOrderIsWaitingAccept.remove(order);
                    List<OrderDelivery> list = mapOrderDeliveryForShipper.get(order.getShipper().getUsername());
                    if (list != null) {
                        for (int i = list.size() - 1; i >= 0; i--) {
                            if (order.getId().equals(list.get(i).getId())) {
                                list.remove(i);
                                break;
                            }
                        }
                    }
                }

                order.setStatus(order.getStatus() * -1);
                order.setAuthor(accountId);
                order.setRefundCost(order.getTotalCost());
                order.setShipper(null);
                order.setLastUpdate(new Time(new Date().getTime()));

                 {
                    try {
                        transactionListener.updateRefundTransaction(accountId, accountId, order.getTotalCost(), order.getStatus(), order.getId());
                        accountListener.updateWalletAccount(accountId, order.getTotalCost());
                        accountListener.updateNumCancel(accountId, 1);
                        dao.CancelOrder(order);

                    } catch (ClassNotFoundException | SQLException e) {
                        Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "OrderController - Cancel Cust: {0}", e.getMessage());
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                return new ResponseEntity<>("Đơn hàng " + orderId + " của bạn đã được hủy thành công", HttpStatus.OK);
            default:
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    class OrderService {

        Order checkOrderIsInProgress(OrderShipper order) {
            return OrderController.mapOrderInProgress.values()
                    .stream()
                    .filter(o -> order.getId().equals(o.getId()))
                    .findFirst()
                    .orElse(null);
        }

        List<Order> checkOrdersWaitingAfterShoppingOfShipper(List<OrderShipper> listOrders) {
            return mapOrderWaitingAfterShopping.get(listOrders.get(0).getShipper());
        }

        void removeOrderBelongToShipper(Shipper shipper, Order order) {
            List<String> list = mapShipperOrdersInProgress.get(shipper.getUsername());
            if (list == null) {
                return;
            }
            list.remove(order.getId());
        }

        void removeOrderDeliveryForShipper(Shipper shipper, Order order) {
            List<OrderDelivery> list = mapOrderDeliveryForShipper.get(shipper.getUsername());
            if (list != null) {
                Iterator<OrderDelivery> iterator = list.iterator();
                while (iterator.hasNext()) {
                    OrderDelivery tmp = iterator.next();
                    if (tmp.getId().equals(order.getId())) {
                        iterator.remove();
                    }
                }

//                if (list.isEmpty()) {
//                    mapOrderDeliveryForShipper.remove(shipper.getUsername());
//                }
            }
        }

        void removePreOrderOfShipper(Shipper shipper, Order order) {
            List<Order> list = mapOrderWaitingAfterShopping.get(shipper.getUsername());
            if (list == null) {
                return;
            }
            list.remove(order);
            if (list.isEmpty()) {
                mapOrderWaitingAfterShopping.remove(shipper.getUsername());
            }
        }

        void checkShipperOutOfProgress() {
            List<String> list = mapInProgressShipper.keySet().stream().collect(Collectors.toList());
            for (int i = list.size() - 1; i >= 0; i--) {
                if (mapShipperOrdersInProgress.get(list.get(i)) == null) {
                    shipperListener.changeStatusOfShipper(list.get(i));
                }
            }
        }

        String generateId(OrderRequestCustomer obj) {
            LocalTime time = LocalTime.now(ZoneId.of("GMT+7"));
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), new Locale("vi", "vn"));
            return obj.getCust()
                    + String.valueOf(cal.get(Calendar.YEAR))
                    + String.valueOf(cal.get(Calendar.MONTH) + 1)
                    + String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
                    + String.valueOf(time.getHour())
                    + String.valueOf(time.getMinute())
                    + String.valueOf(time.getSecond());
        }

        void addOrderInqueue(Order order) {
            coreListener.filterOrder(order);
        }

        List<Shipper> getShippers(Comparator comparator) {
            List<Shipper> list = new ArrayList<>();
            mapAvailableShipper.entrySet().forEach((entry) -> {
                list.add(entry.getValue());
            });
            list.sort(comparator);
            return list;
        }

        Shipper getShipper(OrderShipper order) {
            return shipperListener.getShipper(order.getShipper());
        }

        int getTheShortesDistanceInMechanism() {
            TreeMap<Integer, Integer> sortMechanismReleaseOrder = new TreeMap<>(mapMechanismReleaseOrder);
            return sortMechanismReleaseOrder.get(sortMechanismReleaseOrder.firstKey());
        }

        int getTheLongestDistanceInMechanism() {
            TreeMap<Integer, Integer> sortMechanismReleaseOrder = new TreeMap<>(mapMechanismReleaseOrder);
            return sortMechanismReleaseOrder.get(sortMechanismReleaseOrder.lastKey());
        }

        MatrixObject getDistanceMatrixObject(String[] source, String[] destination) throws IOException {
            UrlConnection url = new UrlConnection();
            return GsonHelper.gson.fromJson(new InputStreamReader(
                    url.openConnectionFromSourceToDestination(source, destination), "utf-8"),
                    MatrixObject.class);
        }

        MatrixObject getDistanceMatrixObject(String[] source, Set<String[]> destination) throws IOException {
            UrlConnection url = new UrlConnection();
            return GsonHelper.gson.fromJson(new InputStreamReader(
                    url.openConnectionFromSourceToDestination(source, destination), "utf-8"),
                    MatrixObject.class);
        }

        MatrixObject getDistanceMatrixObject(String[] source, List<String> destination) throws IOException {
            UrlConnection url = new UrlConnection();
            return GsonHelper.gson.fromJson(new InputStreamReader(
                    url.openConnectionFromSourceToDestination(source, destination), "utf-8"),
                    MatrixObject.class);
        }

        void cancelMapShipperOrder(String orderId, String accountId) {
            List<String> list = mapShipperOrdersInProgress.get(accountId);
            Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                String id = iterator.next();
                if (id.equals(orderId)) {
                    iterator.remove();
                }
            }
            if (list.isEmpty()) {
                mapShipperOrdersInProgress.remove(accountId);
            }
        }

        void cancelMapShipperOrder(String orderId) {
            List<String> listShippers = mapShipperOrdersInProgress.keySet().stream().collect(Collectors.toList());
            if (listShippers != null) {
                for (int i = 0; i < listShippers.size(); i++) {
                    List<String> listOrders = mapShipperOrdersInProgress.get(listShippers.get(i));
                    if (listOrders != null) {
                        for (int j = listOrders.size() - 1; j >= 0; j--) {
                            if (listOrders.get(j).equals(orderId)) {
                                listOrders.remove(j);
                                if (listOrders.isEmpty()) {
                                    mapShipperOrdersInProgress.remove(listShippers.get(i));
                                }
                            }
                        }
                    }
                }
            }
        }

        double getTotalFoodMoneyOfOrder(Order order) {
            double result = 0;
            List<OrderDetail> list = order.getDetails();
            for (OrderDetail ele : list) {
                result += ele.getPricePaid();
            }
            return result;
        }

        OrderDoneDelivery executeOrderIsDone(Shipper shipper, Order order, int status, SyncHelper sync) throws ClassNotFoundException, SQLException {
            Order orderDoneObj = mapOrderInProgress.remove(order.getId());
            mapOrderIsDone.put(orderDoneObj.getId(), orderDoneObj);
            service.removeOrderBelongToShipper(shipper, orderDoneObj);

            double totalReceiveTransMoney = (orderDoneObj.getCostDelivery() * ((100 - order.getCommissionShipping()) / 100.0))
                    + (orderDoneObj.getCostShopping() * ((100 - order.getCommissionShopping()) / 100.0));
            transactionListener.updateDeliveryTransaction(shipper.getUsername(), shipper.getUsername(),
                    totalReceiveTransMoney, status, orderDoneObj.getId());
            accountListener.updateWalletAccount(shipper.getUsername(), totalReceiveTransMoney + getTotalFoodMoneyOfOrder(order));
            accountListener.updateNumSuccess(orderDoneObj.getCust(), 1);

            shipper.setNumDelivery(shipper.getNumDelivery() + 1);
            shipperListener.updateNumSuccess(shipper.getUsername(), 1);

            return sync.syncOrderSystemToOrderDoneDelivery(orderDoneObj);
        }

        String updateOrder(Shipper shipper, Order order, int status) {
            try {
                order.setStatus(status);
                order.setLat(shipper.getLat());
                order.setLng(shipper.getLng());
                order.setLastUpdate(new Time(new Date().getTime()));
                return dao.updatetOrder(order);
            } catch (SQLException | ClassNotFoundException e) {
                Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "OrderController - Update Order: {0}", e.getMessage());
            }
            return null;
        }

        void sendNotificationOrderToShipper(String token, String orderId) {
            Map<String, String> map = new HashMap<>();
            map.put("isCancel", "true");
            map.put("orderId", orderId);

            Firebase firebase = new Firebase();
            try {
                String result = firebase.pushNotifyOrdersToShipper(token, map);
                System.out.println("Firebase: " + result);
            } catch (FirebaseMessagingException | IOException | NullPointerException | IllegalArgumentException e) {
                Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Send Notification: {0}", e.getMessage());
            }
        }
        /*
        private String splitLocation(String location) {
            return location.split("\\.")[0];
        }

        private String checkOrderShipperRejected(List<String> listOrderReject, String orderId) {
            return listOrderReject.stream()
                    .filter(id -> orderId.equals(id))
                    .findFirst()
                    .orElse(null);
        }

        private int getTheShortestDistance(List<String> listDistanceValue) {
            int index = 0;
            long tmp = Long.parseLong(listDistanceValue.get(0));
            for (int i = 1; i < listDistanceValue.size(); i++) {
                if (tmp > Long.parseLong(listDistanceValue.get(i))) {
                    index = i;
                    tmp = Long.parseLong(listDistanceValue.get(i));
                }
            }
            return index;
        }

        private int getTimeShopping(List<OrderDetail> list) {
            double weightDefault = 0.5;
            int timeDefault = 10;
            int totalTimeShopping = 0;
            for (OrderDetail orderDetail : list) {
                totalTimeShopping += timeDefault;
                double weight = orderDetail.getWeight();
                totalTimeShopping += (int) (weight / weightDefault) >= 1 ? (int) (weight / weightDefault - 1) * 5 : 0;
            }
            return totalTimeShopping;
        }

        private int getTimeDelivery(List<String> listDurationValue, int index) {
            int timeSecondDelivery = Integer.parseInt(listDurationValue.get(index));
            return timeSecondDelivery / 60;
        }

        private List<String> getListDestinationAddress(List<Order> listOrders) {
            List<String> listResult = new ArrayList<>();
            for (Order order : listOrders) {
                listResult.add(order.getAddressDelivery());
            }
            return listResult;
        }

        private Map<String[], List<Order>> groupOrders(Shipper shipper, Map<String, Order> mapOrders) {
            Map<String[], List<Order>> map = new HashMap<>();
            List<String> listOrderReject = mapOrdersShipperReject.get(shipper.getUsername());
            for (Order order : mapOrders.values()) {
                boolean flag = true;
                if (listOrderReject != null) {
                    flag = checkOrderShipperRejected(listOrderReject, order.getId()) == null;
                }
                if (flag) {
                    String lat = MarketController.mapMarket.get(order.getMarket()).getLat();
                    String lng = MarketController.mapMarket.get(order.getMarket()).getLng();

                    if (splitLocation(shipper.getLat()).equals(splitLocation(lat))) {
                        if (splitLocation(shipper.getLng()).equals(splitLocation(lng))) {
                            String[] tmp = null;
                            for (String[] arr : map.keySet()) {
                                if (arr[0].equals(lat)) {
                                    if (arr[1].equals(lng)) {
                                        tmp = arr;
                                    }
                                }
                            }
                            List<Order> listTmp;
                            if (tmp == null) {
                                tmp = new String[2];
                                tmp[0] = lat;
                                tmp[1] = lng;
                                listTmp = new ArrayList<>();
                            } else {
                                listTmp = map.get(tmp);
                            }
                            listTmp.add(order);
                            map.put(tmp, listTmp);
                        }
                    }
                }
            }
            return map;
        }

        private String[] extractLocationMarket(List<Order> mapOrdersNearShipper) {
            List<Order> list = new ArrayList(mapOrdersNearShipper);
            String lat = MarketController.mapMarket.get(list.get(0).getMarket()).getLat();
            String lng = MarketController.mapMarket.get(list.get(0).getMarket()).getLng();
            return new String[]{lat, lng};
        }

        private Map<long[], List<Order>> filterOrderNearShipper(Map<String[], List<Order>> ordersLocation,
                MatrixObject distanceObj) {
            ExtractElementDistanceMatrixApi extract = new ExtractElementDistanceMatrixApi();
            List<ElementObject> listElments = extract.getListElements(distanceObj);
            List<String> listDistanceValue = extract.getListDistance(listElments, "value");
            List<String> listDurationValue = extract.getListDuration(listElments, "value");

            int index = getTheShortestDistance(listDistanceValue);

            if (Integer.parseInt(listDistanceValue.get(index)) > getTheShortesDistanceInMechanism()) {
                return null;
            }

            Map<long[], List<Order>> mapOrdersNearShipper = new HashMap<>();
            int position = -1;
            if (Integer.parseInt(listDistanceValue.get(index)) <= getTheShortesDistanceInMechanism()) {
                for (Map.Entry<String[], List<Order>> entry : ordersLocation.entrySet()) {
                    if (++position == index) {
                        long[] key = {Long.parseLong(listDistanceValue.get(index)), Long.parseLong(listDurationValue.get(index))};
                        mapOrdersNearShipper.put(key, entry.getValue());
                    }
                }
            }
            return mapOrdersNearShipper;
        }

        private Map<Order, int[]> filterOrderOnTimeToRelease(Map<long[], List<Order>> mapOrderNearShipper,
                MatrixObject matrixMarketToDestinationObj) {
            ExtractElementDistanceMatrixApi extract = new ExtractElementDistanceMatrixApi();
            List<ElementObject> listElments = extract.getListElements(matrixMarketToDestinationObj);
            List<String> listDistanceValue = extract.getListDistance(listElments, "value");
            List<String> listDurationValue = extract.getListDuration(listElments, "value");
            Map<Order, int[]> map = new HashMap<>();

            mapOrderNearShipper.entrySet().forEach((entry) -> {
                int index = 0;
                for (Order order : entry.getValue()) {
                    int timeShopping = getTimeShopping(order.getDetails());
                    int timeDelivery = getTimeDelivery(listDurationValue, index);
                    int timeGoing = (int) entry.getKey()[1];
                    int totalTime = timeGoing + timeDelivery + timeShopping > 180 ? timeGoing + timeDelivery + timeShopping : 180;
                    System.out.println("Order: " + order.getId() + " - Time: " + totalTime);
                    map.put(order, new int[]{totalTime, Integer.parseInt(listDistanceValue.get(index))});
                    index++;
                }
            });
            return map;
        }

        private void preProcessOrderRelease(Shipper shipper, Map<Order, Integer> map) {
            List<OrderDelivery> list = new ArrayList();
            for (Map.Entry<Order, Integer> entry : map.entrySet()) {
                Order order = mapOrderInQueue.remove(entry.getKey().getId());
                order.setShipper(shipper.getUsername());
                mapOrderIsWaitingAccept.put(order, SystemTime.SYSTEM_TIME + (20 * 1000));
                list.add(new OrderDelivery(order.getAddressDelivery(),
                        entry.getValue(),
                        order));
                int numRelease = mapCountOrderRelease.getOrDefault(order.getId(), 0);
                mapCountOrderRelease.put(order.getId(), numRelease + 1);
            }
            mapOrderDeliveryForShipper.put(shipper.getUsername(), list);
            shipperListener.changeStatusOfShipper(shipper.getUsername());
        }

        private void sendNotificationOrderToShipper(String shipperId) {
            IShipper shipperListener = new ShipperController().getShipperListener();
            Map<String, String> map = new HashMap<>();
            map.put("compulsory", "false");

            Firebase firebase = new Firebase();
            try {
                String result = firebase.pushNotifyOrdersToShipper(shipperListener.getShipper(shipperId).getTokenFCM(), map);
                System.out.println("Firebase: " + result);
            } catch (FirebaseMessagingException | IOException | IllegalArgumentException e) {
                Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Send Notification: {0}", e.getMessage());
            }
        }

        @Override
        public void scanOrdesrReleaseToShippers() {
            System.out.println("Scan Order Release For Shipper");
            if (mapOrderInQueue.isEmpty()) {
                return;
            }
            MatrixObject distanceObj;
            List<Shipper> listShipper = getShippers(new SortByHighActive());

            synchronized (listShipper) {
                for (Iterator<Shipper> it = listShipper.iterator(); it.hasNext();) {
                    Shipper shipper = it.next();
                    if (shipper.getLat() != null && shipper.getLng() != null) {
                        try {
                            Map<String[], List<Order>> locationOrders = groupOrders(shipper, mapOrderInQueue);
                            distanceObj = getDistanceMatrixObject(new String[]{shipper.getLat(), shipper.getLng()}, locationOrders.keySet());

                            Map<long[], List<Order>> mapOrdersNearShipper = filterOrderNearShipper(locationOrders, distanceObj);
                            if (mapOrdersNearShipper != null) {
                                List<Order> listOrder = mapOrdersNearShipper.values()
                                        .iterator()
                                        .next();
                                String[] locationMarket = extractLocationMarket(listOrder);
                                distanceObj = getDistanceMatrixObject(locationMarket, getListDestinationAddress(listOrder));
                                Map<Order, int[]> mapResult = filterOrderOnTimeToRelease(mapOrdersNearShipper, distanceObj);

                                if (!mapResult.isEmpty()) {
                                    int count = 0;
                                    DateTimeHelper helper = new DateTimeHelper();
                                    Map<Order, Integer> mapOrdersResult = null;
                                    for (Map.Entry<Order, int[]> entry : mapResult.entrySet()) {
                                        if (helper.calculateTimeForShipper(entry.getKey(), entry.getValue()[0])) {
                                            if (++count > shipper.getMaxOrder()) {
                                                break;
                                            }
                                            if (mapOrdersResult == null) {
                                                mapOrdersResult = new HashMap<>();
                                            }
                                            mapOrdersResult.put(entry.getKey(), entry.getValue()[1]);
                                        }
                                    }

                                    if (mapOrdersResult != null) {
                                        System.out.println("Shipper = " + shipper.getUsername() + "is has Orders");
                                        for (Map.Entry<Order, Integer> entry : mapOrdersResult.entrySet()) {
                                            System.out.println(entry.getKey());
                                        }
                                        it.remove();
                                        preProcessOrderRelease(shipper, mapOrdersResult);
                                        sendNotificationOrderToShipper(shipper.getUsername());
                                    }
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Scan Order, UnsupportedEncodingException: {0}", e.getMessage());
                        } catch (IOException | NullPointerException e) {
                            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Scan Order, IO - NullPointer: {0}", e.getMessage());
                        }
                    }
                }
            }
            System.out.println("");
        }
         */
    }
}
