package com.smhu.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.smhu.google.Firebase;
import com.smhu.helper.DateTimeHelper;
import com.smhu.iface.IOrder;
import com.smhu.order.Order;
import com.smhu.response.ResponseMsg;
import com.smhu.response.customer.OrderCustomer;
import com.smhu.order.OrderDetail;
import com.smhu.order.TimeTravel;
import com.smhu.response.shipper.OrderShipper;
import com.smhu.schedule.SystemTime;
import com.smhu.utils.DBUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public static Map<Order, Integer> mapOrderInQueue = new HashMap<>();
    public static Map<String, Order> mapOrderInProcess = new HashMap<>();
    public static Map<String, Order> mapOrderInCancel = new HashMap<>();

    IOrder orderListener = new OrderService();
    OrderService service = new OrderService();

    @PostMapping("/order")
    public ResponseEntity<?> newOrder(@RequestBody OrderCustomer orderReceive) {
        String result = "";
        try {
            int status = StatusController.mapStatus.keySet()
                    .stream()
                    .filter((s) -> {
                        return s.toString().matches("1\\d");
                    })
                    .mapToInt((value) -> {
                        return value;
                    }).max().getAsInt();

            Order order = new Order();
            order.setId(new OrderService().generateId(orderReceive));
            order.setCust(orderReceive.getCust());
            order.setAddressDelivery(orderReceive.getAddressDelivery());
            order.setNote(orderReceive.getNote());
            order.setMarket(orderReceive.getMarket());
            order.setCreateDate(new java.sql.Date(new Date().getTime()));
            order.setCreateTime(new Time(new Date().getTime()));
            order.setLastUpdate(new Time(new Date().getTime()));
            order.setStatus(status);
            order.setCostShopping(orderReceive.getCostShopping());
            order.setCostDelivery(orderReceive.getCostDelivery());
            order.setTotalCost(orderReceive.getTotalCost());
            order.setDateDelivery(orderReceive.getDateDelivery());
            order.setTimeDelivery(orderReceive.getTimeDelivery());
            order.setTimeTravel(orderReceive.getTimeTravel());
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
            if (service.insertOrderTimeTravel(result, order.getTimeTravel()) == 0) {
                return new ResponseEntity<>(new ResponseMsg("Insert new order time travel failed"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            service.addOrderInqueue(order);
            service.checkOrderInqueue();

        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ResponseMsg(result), HttpStatus.OK);
    }

    @PutMapping("/orders/update")
    public ResponseEntity<?> updatetOrder(@RequestBody List<OrderShipper> listOrders) {
        List<OrderShipper> listResults = new ArrayList<>();
        for (OrderShipper orderInProcess : listOrders) {
            Order order = mapOrderInProcess.get(orderInProcess.getId());
            if (order == null) {
                return new ResponseEntity<>(new ResponseMsg("Đơn hàng của bạn đã hoàn thành hoặc bị hủy"), HttpStatus.NOT_FOUND);
            }

            int status;
            try {
                if (order.getShipper() == null || order.getShipper().isEmpty()) {
                    order.setShipper(orderInProcess.getShipper());
                }

                String[] location = ShipperController.mapLocationAvailableShipper.remove(order.getShipper());
                ShipperController.mapLocationInProgressShipper.put(order.getShipper(), location);

                if (!String.valueOf(order.getStatus()).matches("2\\d")) {
                    status = StatusController.mapStatus.keySet()
                            .stream()
                            .filter(t -> String.valueOf(t).matches("2\\d"))
                            .sorted()
                            .findFirst()
                            .orElse(21);
                } else {
                    status = order.getStatus() + 1;
                }
                order.setStatus(status);
                order.setLastUpdate(new Time(new Date().getTime()));

                service.updatetOrder(order);

                listResults.add(service.syncOrderSystemToOrderShipper(order));

                int tmp = StatusController.mapStatus.keySet()
                        .stream()
                        .filter(t -> String.valueOf(t).matches("2\\d"))
                        .mapToInt(value -> value)
                        .max()
                        .getAsInt();
                if (status == tmp) {
                    mapOrderInProcess.remove(order.getId());
                }
            } catch (SQLException | ClassNotFoundException e) {
                Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
                return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (status == 21) {
                Map<String, String> token = new HashMap<>();
                token.put("cust_id", order.getCust());
                Map<String, String> values = new HashMap<>();
                values.put("order_id", order.getId());
                values.put("msg", "Đơn hàng của bạn đã có shipper nhận");
                try {
                    new Firebase().pushNotifyByToken(token, values);
                } catch (FirebaseMessagingException | IOException e) {
                    Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
                }
            }
        }
        return new ResponseEntity<>(listResults, HttpStatus.OK);
    }

    @GetMapping("/tracking/shipper/{shipperId}/lat/{lat}/lng/{lng}")
    public ResponseEntity trackingOrder(@PathVariable("shipperId") String shipperId,
            @PathVariable("lat") String lat, @PathVariable("lng") String lng) {
        Firebase firebase = new Firebase();

        mapOrderInProcess.values().stream()
                .filter((t) -> {
                    return shipperId.equals(t.getShipper());
                })
                .forEach((t) -> {
                    try {
                        firebase.pushNotifyLocationOfShipperToCustomer(t.getId(), lat, lng);
                    } catch (FirebaseMessagingException | IOException e) {
                        Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
                    }
                });

        return new ResponseEntity(HttpStatus.OK);
    }

    private final String STAFF = "STAFF";
    private final String SHIPPER = "SHIPPER";

    @DeleteMapping("order/delete/{orderId}/{type}/{personId}")
    public ResponseEntity<?> cancelOrder(@PathVariable("orderId") String orderId, @PathVariable("type") String type,
            @PathVariable("personId") String personId) {
        Order order = null;

        switch (type.toUpperCase()) {
            case STAFF:
                order = mapOrderInCancel.remove(orderId);
                if (order == null) {
                    order = mapOrderInProcess.remove(orderId);
                }

                String result = null;
                try {
                    order.setAuthor(personId);
                    order.setLastUpdate(new Time(new Date().getTime()));
                    result = service.CancelOrder(order);
                } catch (ClassNotFoundException | SQLException e) {
                    Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
                }
                if (result == null) {
                    return new ResponseEntity<>(new ResponseMsg("Error while cancel order. Please try again!"), HttpStatus.INTERNAL_SERVER_ERROR);
                }

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
                order = mapOrderInProcess.remove(orderId);

                order.setStatus(order.getStatus() * -1);
                mapOrderInCancel.put(order.getId(), order);
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

        @Override
        public void checkOrderInqueue() {
            List<Order> tmp = new ArrayList<>();
            for (Map.Entry<Order, Integer> order : OrderController.mapOrderInQueue.entrySet()) {
                int totalMinuteCurrent = DateTimeHelper.parseTimeToMinute(new Time(SystemTime.SYSTEM_TIME).toLocalTime());
                if (totalMinuteCurrent >= order.getValue() - 180 && totalMinuteCurrent <= order.getValue()) {
                    mapOrderInProcess.put(order.getKey().getId(), order.getKey());
                    System.out.println(order);
                    tmp.add(order.getKey());
                }
            }

            for (Order order : tmp) {
                OrderController.mapOrderInQueue.remove(order);
            }
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
            mapOrderInQueue.put(order, getTimeForShipper(order));
        }

        int getTimeForShipper(Order order) {
            return new DateTimeHelper().calculateTimeForShipper(order, order.getTimeTravel());
        }

        String insertOrder(Order order) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "INSERT INTO ORDERS (ID, CUST, ADDRESS_DELIVERY, MARKET, NOTE, \n"
                            + "COST_SHOPPING, COST_DELIVERY, TOTAL_COST, \n"
                            + "CREATED_DATE, CREATED_TIME, LAST_UPDATE, STATUS,  \n"
                            + "DATE_DELIVERY, TIME_DELIVERY)\n"
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, order.getId());
                    stmt.setString(2, order.getCust());
                    stmt.setString(3, order.getAddressDelivery());
                    stmt.setString(4, order.getMarket());
                    stmt.setString(5, order.getNote());

                    stmt.setDouble(6, order.getCostShopping());
                    stmt.setDouble(7, order.getCostDelivery());
                    stmt.setDouble(8, order.getTotalCost());

                    stmt.setDate(9, order.getCreateDate());
                    stmt.setTime(10, order.getCreateTime());
                    stmt.setTime(11, order.getLastUpdate());
                    stmt.setInt(12, order.getStatus());

                    stmt.setDate(13, order.getDateDelivery());
                    stmt.setTime(14, order.getTimeDelivery());
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
                        stmt.setString(2, detail.getFood());
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
    }
}
