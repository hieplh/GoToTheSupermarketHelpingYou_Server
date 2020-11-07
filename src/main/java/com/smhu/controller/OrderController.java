package com.smhu.controller;

import static com.smhu.controller.ShipperController.mapMechanismReleaseOrder;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.smhu.GototheSupermarketHelpingYouApplication;
import com.smhu.account.Account;
import com.smhu.account.Customer;
import com.smhu.account.Shipper;
import com.smhu.comparator.SortByHighActive;
import com.smhu.comparator.SortByLowActive;
import com.smhu.google.Firebase;
import com.smhu.google.matrixobj.DistanceMatrixObject;
import com.smhu.google.matrixobj.ElementObject;
import com.smhu.helper.DateTimeHelper;
import com.smhu.helper.ExtractElementDistanceMatrixApi;
import com.smhu.helper.GsonHelper;
import com.smhu.iface.IAccount;
import com.smhu.iface.IMain;
import com.smhu.iface.IOrder;
import com.smhu.iface.IShipper;
import com.smhu.iface.IStatus;
import com.smhu.order.Order;
import com.smhu.response.ResponseMsg;
import com.smhu.response.customer.OrderCustomer;
import com.smhu.order.OrderDetail;
import com.smhu.order.TimeTravel;
import com.smhu.response.moderator.OrderOverall;
import com.smhu.response.shipper.OrderDelivery;
import com.smhu.response.shipper.OrderShipper;
import com.smhu.system.SystemTime;
import com.smhu.url.UrlConnection;
import com.smhu.utils.DBUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {

    public final static Map<String, Order> mapOrderInQueue = new HashMap<>();

    public final static Map<Order, Long> mapOrderIsWaitingAccept = new HashMap<>();
    public final static Map<String, List<OrderDelivery>> mapOrderDeliveryForShipper = new HashMap<>();

    public final static Map<String, Integer> mapCountOrderRelease = new HashMap<>();

    public final static Map<String, Order> mapOrderInProgress = new HashMap<>();
    public final static Map<String, Order> mapOrderIsDone = new HashMap<>();

    public final static Map<String, List<String>> mapOrdersShipperReject = new HashMap<>();
    public final static Map<String, Order> mapOrderIsCancelInQueue = new HashMap<>();
    public final static Map<String, Order> mapOrderIsCancel = new HashMap<>();

    private final OrderService service;

    private final IOrder orderListener;
    private final IShipper shipperListener;
    private final IStatus statusListener;
    private final IMain mainListener;
    private final IAccount accountListener;

    private final String CUSTOMER = "CUSTOMER";
    private final String STAFF = "STAFF";
    private final String SHIPPER = "SHIPPER";

    public OrderController() {
        service = new OrderService();
        orderListener = new OrderService();
        statusListener = new StatusController().getStatusListener();
        mainListener = new GototheSupermarketHelpingYouApplication().getMainListener();
        shipperListener = new ShipperController().getShipperListener();
        accountListener = new AccountController().getAccountListener();
    }

    @GetMapping("order/{type}/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable("id") String id, @PathVariable("type") String type) {
        try {
            Order order = service.getOrderById(id, STAFF);
            if (order == null) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
            mainListener.loadOrderDetail(order);
            switch (type.toUpperCase()) {
                case CUSTOMER:
                    return new ResponseEntity<>(order, HttpStatus.OK);
                case STAFF:
                    OrderOverall orderStaff = new OrderOverall(order);
                    orderStaff.setMarket(MarketController.mapMarket.get(order.getMarket()));
                    orderStaff.setCustomer((Customer) accountListener.getAccountById(order.getCust(), CUSTOMER));
                    Shipper shipper = shipperListener.getShipper(order.getShipper());
                    if (shipper == null) {
                        shipper = (Shipper) accountListener.getAccountById(order.getShipper(), SHIPPER);
                    }
                    orderStaff.setShipper(shipper);
                    if (order.getAuthor() != null) {
                        orderStaff.setAuthor((Account) accountListener.getAccountById(order.getAuthor(), STAFF));
                    }
                    return new ResponseEntity<>(orderStaff, HttpStatus.OK);
                default:
                    return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("orders/all")
    public ResponseEntity<?> getAllOrders() {
        List<Order> list = new ArrayList<>();
        list.addAll(mapOrderInQueue.values());
//        list.addAll(mapOrderIsWaitingRelease.values());
        list.addAll(mapOrderIsWaitingAccept.keySet().stream().collect(Collectors.toList()));
        list.addAll(mapOrderInProgress.values());
        list.addAll(mapOrderIsDone.values());
        list.addAll(mapOrderIsCancel.values());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("switch/{orderId}")
    public ResponseEntity<?> changeShipper(@PathVariable("orderId") String orderId) {
        DistanceMatrixObject distanceObj;
        List<Shipper> listShipper = service.getShippers(new SortByLowActive());
        if (listShipper.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }

        synchronized (listShipper) {
            Order order = mapOrderInProgress.get(orderId);
            Shipper oldShipper = shipperListener.getShipper(order.getShipper());
            for (Iterator<Shipper> it = listShipper.iterator(); it.hasNext();) {
                Shipper newShipper = it.next();
                if (newShipper.getLat() != null && newShipper.getLng() != null) {
                    try {
                        String[] sourceLocation = new String[]{oldShipper.getLat(), oldShipper.getLng()};
                        String[] destinationLocation = new String[]{newShipper.getLat(), newShipper.getLng()};

                        distanceObj = service.getDistanceMatrixObject(sourceLocation, destinationLocation);
                        ExtractElementDistanceMatrixApi extract = new ExtractElementDistanceMatrixApi();
                        List<ElementObject> listElments = extract.getListElements(distanceObj);
                        List<String> listDistanceValue = extract.getListDistance(listElments, "value");

                        int range = service.getTheLongestDistanceInMechanism();
                        for (String distanceString : listDistanceValue) {
                            int distance = Integer.parseInt(distanceString);
                            if (distance <= range) {
                                order.setShipper(newShipper.getId());
                                order.setLastUpdate(new Time(new Date().getTime()));
                                service.updatetOrder(order);

                                it.remove();
                                shipperListener.addShipper(newShipper);
                                System.out.println("Change shipper from: " + oldShipper.getId() + " to: " + newShipper.getId());
                                return new ResponseEntity<>(order, HttpStatus.OK);
                            }
                        }
                    } catch (IOException | ClassNotFoundException | NumberFormatException | SQLException e) {
                        Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Scan Order: {0}", e.getMessage());
                    }
                }
            }
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/order")
    public ResponseEntity<?> newOrder(@RequestBody OrderCustomer orderReceive) {
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
            order.setMarket(orderReceive.getMarket());
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

            result = service.insertOrder(order);

            if (result == null) {
                return new ResponseEntity<>(new ResponseMsg("Insert new order failed"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (service.insertOrderDetail(result, order.getDetails()) == null) {
                return new ResponseEntity<>(new ResponseMsg("Insert new order detail failed"), HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                int count = 0;
                for (OrderDetail detail : order.getDetails()) {
                    detail.setId(result + String.valueOf(++count));
                }
            }
//            if (service.insertOrderTimeTravel(result, order.getTimeTravel()) == 0) {
//                return new ResponseEntity<>(new ResponseMsg("Insert new order time travel failed"), HttpStatus.INTERNAL_SERVER_ERROR);
//            }

            service.addOrderInqueue(order);

        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ResponseMsg(result), HttpStatus.OK);
    }

    @PutMapping("/orders/update")
    public ResponseEntity<?> updatetOrder(@RequestBody List<OrderShipper> listOrders) {
        List<OrderShipper> listResults = new ArrayList<>();
        Shipper shipper = null;
        int status;
        boolean isFirstAcceptOrder = false;
        boolean orderIsDone = false;
        for (OrderShipper orderInProcess : listOrders) {
            Order order = service.checkOrderIsInProgress(orderInProcess);
            if (order == null) {
                Order orderWaitAccept = mapOrderIsWaitingAccept.keySet()
                        .stream()
                        .filter(o -> orderInProcess.getId().equals(o.getId()))
                        .findFirst()
                        .orElse(null);

                if (orderWaitAccept == null || !orderWaitAccept.getShipper().equals(orderInProcess.getShipper())) {
                    return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
                }

                if (shipper == null) {
                    shipper = shipperListener.getShipper(orderWaitAccept.getShipper());
                }
                isFirstAcceptOrder = true;
                mapOrderIsWaitingAccept.remove(orderWaitAccept);
                mapCountOrderRelease.remove(orderWaitAccept.getId());

                order = orderWaitAccept;
                mapOrderInProgress.put(order.getId(), order);
            }

            if (order.getShipper() == null) {
                return new ResponseEntity<>(new ResponseMsg("Đơn hàng của bạn đã hoàn thành hoặc bị hủy"), HttpStatus.NOT_FOUND);
            }

            try {
//                if (order.getShipper() == null || order.getShipper().isEmpty() || order.getShipper().equalsIgnoreCase("null")) {
//                    order.setShipper(orderInProcess.getShipper());
//                }

                int tmpStatus = statusListener.getStatusIsDoneOrder();
                if (!String.valueOf(order.getStatus()).matches("2\\d")) {
                    status = statusListener.getStatusIsAccept();
                } else {
                    status = order.getStatus() + 1;
                }

                if (status == tmpStatus) {
                    Order orderClone = mapOrderInProgress.remove(order.getId());
                    mapOrderIsDone.put(orderClone.getId(), orderClone);
                    orderIsDone = true;
                    if (shipper == null) {
                        shipper = shipperListener.getShipper(order.getShipper());
                    }
                }

                order.setStatus(status);
                order.setLastUpdate(new Time(new Date().getTime()));
                service.updatetOrder(order);
                listResults.add(service.syncOrderSystemToOrderShipper(order));
            } catch (SQLException | ClassNotFoundException e) {
                Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
                return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (status == 21) {
                Map<String, String> token = new HashMap<>();
                token.put("cust_id", order.getCust());
                Map<String, String> values = new HashMap<>();
                values.put("msg", "Đơn hàng của bạn đã có shipper nhận");
                values.put("order_id", order.getId());

//                try {
//                    new Firebase().pushNotifyByToken(token, values);
//                } catch (FirebaseMessagingException | IOException e) {
//                    Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
//                }
            }
        } // end for
        if (isFirstAcceptOrder) {
            mapOrderDeliveryForShipper.remove(shipper.getId());
        } else if (orderIsDone) {
            shipperListener.changeStatusOfShipper(shipper.getId());
        }
        return new ResponseEntity<>(listResults, HttpStatus.OK);
    }

    @GetMapping("/tracking/shipper/{shipperId}/lat/{lat}/lng/{lng}")
    public ResponseEntity trackingOrder(@PathVariable("shipperId") String shipperId,
            @PathVariable("lat") String lat, @PathVariable("lng") String lng) {
//        Firebase firebase = new Firebase();
//
//        mapOrderIsWaitingRelease.values().stream()
//                .filter((t) -> {
//                    return shipperId.equals(t.getShipper());
//                })
//                .forEach((t) -> {
//                    try {
//                        firebase.pushNotifyLocationOfShipperToCustomer(t.getId(), lat, lng);
//                    } catch (FirebaseMessagingException | IOException e) {
//                        Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
//                    }
//                });

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("delete/{orderId}/{type}/{personId}")
    public ResponseEntity<?> cancelOrder(@PathVariable("orderId") String orderId, @PathVariable("type") String type,
            @PathVariable("personId") String personId) {
        Order order;

        switch (type.toUpperCase()) {
            case STAFF:
                order = mapOrderIsCancelInQueue.remove(orderId);
                if (order == null) {
                    order = mapOrderInProgress.remove(orderId);
                }
                mapOrderIsCancel.put(order.getId(), order);

                String result = null;
                try {
                    order.setAuthor(personId);
                    order.setLastUpdate(new Time(new Date().getTime()));
                    order.setStatus(order.getStatus() * -1);
                    result = service.CancelOrder(order);
                } catch (ClassNotFoundException | SQLException e) {
                    Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
                }
                if (result == null) {
                    return new ResponseEntity<>(new ResponseMsg("Error while cancel order. Please try again!"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                shipperListener.changeStatusOfShipper(order.getShipper());

                Map<String, String> token = new HashMap<>();
                token.put("shipper_id", order.getShipper());
                Map<String, String> values = new HashMap<>();
                values.put("msg", "Your cancel request, order_id: " + order.getId() + ", is canceled");
                try {
                    new Firebase().pushNotifyByToken(token, values);
                } catch (FirebaseMessagingException | IOException e) {
                    Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
                }

                return new ResponseEntity<>(new ResponseMsg("Order_id: " + result + ", is canceled"), HttpStatus.OK);
            case SHIPPER:
                order = mapOrderInProgress.remove(orderId);
                order.setStatus(order.getStatus() * -1);
                mapOrderIsCancelInQueue.put(order.getId(), order);
                return new ResponseEntity<>(new ResponseMsg("Your cancel request, order_id: " + order.getId()
                        + ", is processing"), HttpStatus.OK);
            default:
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    public IOrder getOrderListener() {
        return orderListener;
    }

    class OrderService implements IOrder {

        Order checkOrderIsInProgress(OrderShipper order) {
            return OrderController.mapOrderInProgress.values()
                    .stream()
                    .filter(o -> order.getId().equals(o.getId()))
                    .findFirst()
                    .orElse(null);
        }

        String generateId(OrderCustomer obj) {
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
            mapOrderInQueue.put(order.getId(), order);
        }

        public Order getOrderById(String orderId, String type) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            Order order = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT *\n"
                            + "FROM GET_ORDER_BY_ID\n"
                            + "WHERE ID = ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, orderId);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        order = new Order();
                        order.setId(rs.getString("ID"));
                        if (type.toUpperCase().equals(STAFF)) {
                            order.setCust(rs.getString("CUST"));
                        } else {
                            order.setCust(rs.getString("CUST_NAME"));
                        }
                        order.setAddressDelivery(rs.getString("ADDRESS_DELIVERY"));
                        order.setNote(rs.getString("NOTE"));
                        if (type.toUpperCase().equals(STAFF)) {
                            order.setMarket(rs.getString("MARKET"));
                        } else {
                            order.setMarket(rs.getString("MARKET_NAME"));
                        }

                        if (type.toUpperCase().equals(STAFF)) {
                            order.setShipper(rs.getString("SHIPPER"));
                        } else {
                            order.setShipper(rs.getString("SHIPPER_NAME"));
                        }
                        order.setCreateDate(rs.getDate("CREATED_DATE"));
                        order.setCreateTime(rs.getTime("CREATED_TIME"));
                        order.setStatus(rs.getInt("STATUS"));
                        order.setAuthor(rs.getString("AUTHOR"));
                        order.setReasonCancel(rs.getString("REASON_CANCEL"));
                        order.setCostShopping(rs.getDouble("COST_SHOPPING"));
                        order.setCostDelivery(rs.getDouble("COST_DELIVERY"));
                        order.setTotalCost(rs.getDouble("TOTAL_COST"));
                        order.setRefundCost(rs.getDouble("REFUND_COST"));
                        order.setDateDelivery(rs.getDate("DATE_DELIVERY"));
                        order.setTimeDelivery(rs.getTime("TIME_DELIVERY"));
                    }
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            return order;
        }

        String insertOrder(Order order) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "INSERT INTO ORDERS (ID, CUST, ADDRESS_DELIVERY, MARKET, NOTE, \n"
                            + "COST_SHOPPING, COST_DELIVERY, TOTAL_COST, REFUND_COST,\n"
                            + "CREATED_DATE, CREATED_TIME, LAST_UPDATE, STATUS,  \n"
                            + "DATE_DELIVERY, TIME_DELIVERY)\n"
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, order.getId());
                    stmt.setString(2, order.getCust());
                    stmt.setString(3, order.getAddressDelivery());
                    stmt.setString(4, order.getMarket());
                    stmt.setString(5, order.getNote());

                    stmt.setDouble(6, order.getCostShopping());
                    stmt.setDouble(7, order.getCostDelivery());
                    stmt.setDouble(8, order.getTotalCost());
                    stmt.setDouble(9, order.getRefundCost());

                    stmt.setDate(10, order.getCreateDate());
                    stmt.setTime(11, order.getCreateTime());
                    stmt.setTime(12, order.getLastUpdate());
                    stmt.setInt(13, order.getStatus());

                    stmt.setDate(14, order.getDateDelivery());
                    stmt.setTime(15, order.getTimeDelivery());
                    return stmt.executeUpdate() > 0 ? order.getId() : null;
                }
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            return null;
        }

        int[] insertOrderDetail(String idOrder, List<OrderDetail> details) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "INSERT INTO ORDER_DETAIL (ID, FOOD, ORIGINAL_PRICE, SALE_OFF, PAID_PRICE, WEIGHT, DH)\n"
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    stmt = con.prepareStatement(sql);

                    int count = 0;
                    for (OrderDetail detail : details) {
                        stmt.setString(1, idOrder + String.valueOf(++count));
                        stmt.setString(2, detail.getFoodId());
                        stmt.setDouble(3, detail.getPriceOriginal());
                        stmt.setInt(4, detail.getSaleOff());
                        stmt.setDouble(5, detail.getPricePaid());
                        stmt.setDouble(6, detail.getWeight());
                        stmt.setString(7, idOrder);
                        stmt.addBatch();
                    }
                    int[] arr = stmt.executeBatch();
                    con.commit();
                    return arr;
                }
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            return null;
        }

        int insertOrderTimeTravel(String idOrder, TimeTravel time) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "INSERT INTO TIME_TRAVEL (ID, GOING, SHOPPING, DELIVERY, TRAFFIC, DH)\n"
                            + "VALUES (?, ?, ?, ?, ?, ?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, "TIME" + idOrder);
                    stmt.setTime(2, getTimeIfNull(time.getGoing()));
                    stmt.setTime(3, getTimeIfNull(time.getShopping()));
                    stmt.setTime(4, getTimeIfNull(time.getDelivery()));
                    stmt.setTime(5, getTimeIfNull(time.getTraffic()));
                    stmt.setString(6, idOrder);
                    return stmt.executeUpdate();
                }
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            return 0;
        }

        String updatetOrder(Order order) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {

                    String sql = "UPDATE ORDERS SET SHIPPER = ?, STATUS = ?, LAT = ?, LNG = ?, LAST_UPDATE = ?\n"
                            + "WHERE ID = ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, order.getShipper());
                    stmt.setInt(2, order.getStatus());
                    stmt.setString(3, order.getLat());
                    stmt.setString(4, order.getLng());
                    stmt.setTime(5, order.getLastUpdate());
                    stmt.setString(6, order.getId());
                    return stmt.executeUpdate() > 0 ? order.getId() : null;
                }
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            return null;
        }

        String CancelOrder(Order order) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {

                    String sql = "UPDATE ORDERS SET STATUS = ?, AUTHOR = ?, REASON_CANCEL = ?, LAST_UPDATE = ?\n"
                            + "WHERE ID = ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setInt(1, order.getStatus());
                    stmt.setString(2, order.getAuthor());
                    stmt.setString(3, order.getReasonCancel());
                    stmt.setTime(4, order.getLastUpdate());
                    stmt.setString(5, order.getId());
                    return stmt.executeUpdate() > 0 ? order.getId() : null;
                }
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            return null;
        }

        private Time getTimeIfNull(Time time) {
            return time != null ? time : new Time(0, 5, 0);
        }

        private OrderShipper syncOrderSystemToOrderShipper(Order order) {
            OrderShipper obj = new OrderShipper();
            obj.setId(order.getId());
            obj.setCust(order.getCust());
            obj.setAddressDelivery(order.getAddressDelivery());
            obj.setMarket(MarketController.mapMarket.get(order.getMarket()));
            obj.setNote(order.getNote());
            obj.setShipper(order.getShipper());
            obj.setStatus(order.getStatus());
            obj.setCostShopping(order.getCostShopping());
            obj.setCostDelivery(order.getCostDelivery());
            obj.setTotalCost(order.getTotalCost());
            obj.setDateDelivery(order.getDateDelivery());
            obj.setTimeDelivery(order.getTimeDelivery());
            obj.setDetails(order.getDetails());
            return obj;
        }

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

        private int getTheShortesDistanceInMechanism() {
            TreeMap<Integer, Integer> sortMechanismReleaseOrder = new TreeMap<>(mapMechanismReleaseOrder);
            return sortMechanismReleaseOrder.get(sortMechanismReleaseOrder.firstKey());
        }

        private int getTheLongestDistanceInMechanism() {
            TreeMap<Integer, Integer> sortMechanismReleaseOrder = new TreeMap<>(mapMechanismReleaseOrder);
            return sortMechanismReleaseOrder.get(sortMechanismReleaseOrder.lastKey());
        }

        private DistanceMatrixObject getDistanceMatrixObject(String[] source, String[] destination) throws IOException {
            UrlConnection url = new UrlConnection();
            return GsonHelper.gson.fromJson(new InputStreamReader(
                    url.openConnectionFromSourceToDestination(source, destination), "utf-8"),
                    DistanceMatrixObject.class);
        }

        private DistanceMatrixObject getDistanceMatrixObject(String[] source, Set<String[]> destination) throws IOException {
            UrlConnection url = new UrlConnection();
            return GsonHelper.gson.fromJson(new InputStreamReader(
                    url.openConnectionFromSourceToDestination(source, destination), "utf-8"),
                    DistanceMatrixObject.class);
        }

        private DistanceMatrixObject getDistanceMatrixObject(String[] source, List<String> destination) throws IOException {
            UrlConnection url = new UrlConnection();
            return GsonHelper.gson.fromJson(new InputStreamReader(
                    url.openConnectionFromSourceToDestination(source, destination), "utf-8"),
                    DistanceMatrixObject.class);
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
            List<String> listOrderReject = mapOrdersShipperReject.get(shipper.getId());
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
                DistanceMatrixObject distanceObj) {
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
                DistanceMatrixObject matrixMarketToDestinationObj) {
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

        private List<Shipper> getShippers(Comparator comparator) {
            List<Shipper> list = new ArrayList<>();
            for (Map.Entry<String, Shipper> entry : ShipperController.mapAvailableShipper.entrySet()) {
                list.add(entry.getValue());
            }
            list.sort(comparator);
            
            for (Shipper shipper : list) {
                System.out.println(shipper);
            }
            return list;
        }

        private void preProcessOrderRelease(Shipper shipper, Map<Order, Integer> map) {
            List<OrderDelivery> list = new ArrayList();
            for (Map.Entry<Order, Integer> entry : map.entrySet()) {
                Order order = mapOrderInQueue.remove(entry.getKey().getId());
                order.setShipper(shipper.getId());
                mapOrderIsWaitingAccept.put(order, SystemTime.SYSTEM_TIME + (20 * 1000));
                list.add(new OrderDelivery(order.getAddressDelivery(),
                        entry.getValue(),
                        order));
                int numRelease = mapCountOrderRelease.getOrDefault(order.getId(), 0);
                mapCountOrderRelease.put(order.getId(), numRelease + 1);
            }
            mapOrderDeliveryForShipper.put(shipper.getId(), list);
            shipperListener.changeStatusOfShipper(shipper.getId());
        }

        private void sendNotificationOrderToShipper(String shipperId) {
            IShipper shipperListener = new ShipperController().getShipperListener();
            Map<String, String> map = new HashMap<>();
            map.put("compulsory", "false");

            Firebase firebase = new Firebase();
            try {
                String result = firebase.pushNotifyOrdersToShipper(shipperListener.getShipper(shipperId).getTokenFCM(), map);
                System.out.println("Firebase: " + result);
                System.out.println("");
            } catch (FirebaseMessagingException | IOException e) {
                Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "Send Notification: {0}", e.getMessage());
            } catch (IllegalArgumentException e) {

            }
        }

        @Override
        public void scanOrdesrReleaseToShippers() {
            if (mapOrderInQueue.isEmpty()) {
                return;
            }
            DistanceMatrixObject distanceObj;
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
                                        System.out.println("Order Release For Shipper");
                                        System.out.println("Shipper: " + shipper.getId());
                                        for (Map.Entry<Order, Integer> entry : mapOrdersResult.entrySet()) {
                                            System.out.println(entry.getKey());
                                        }
                                        it.remove();
                                        preProcessOrderRelease(shipper, mapOrdersResult);
                                        sendNotificationOrderToShipper(shipper.getId());
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
        }
    }
}
