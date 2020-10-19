package com.smhu.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.smhu.google.Firebase;
import com.smhu.helper.DateTimeHelper;
import com.smhu.iface.IOrder;
import com.smhu.response.ResponseMsg;
import com.smhu.order.Order;
import com.smhu.order.OrderDetail;
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
    public ResponseEntity<?> newOrder(@RequestBody Order obj) {
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

            Order order = new Order(new OrderService().generateId(obj),
                    obj.getCust(),
                    obj.getMarket(),
                    new java.sql.Date(new Date().getTime()),
                    new Time(new Date().getTime()),
                    new Time(new Date().getTime()),
                    String.valueOf(status),
                    obj.getNote(),
                    obj.getCostShopping(),
                    obj.getCostDelivery(),
                    obj.getTotalCost(),
                    obj.getDateDelivery(),
                    obj.getTimeDelivery(),
                    obj.getTimeTravel(),
                    obj.getLat(),
                    obj.getLng(),
                    obj.getDetails());

            result = service.insertOrder(order);

            if (result == null) {
                return new ResponseEntity<>(new ResponseMsg("Insert new order failed"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (service.insertOrderDetail(result, order.getDetails()) == null) {
                return new ResponseEntity<>(new ResponseMsg("Insert new order detail failed"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            service.addOrderInqueue(obj);
            service.checkOrderInqueue();

        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ResponseMsg(result), HttpStatus.OK);
    }

    @PutMapping("/orders/update")
    public ResponseEntity<?> updatetOrder(@RequestBody List<Order> listOrder) {
        List<Order> listResult = new ArrayList<>();
        for (Order orderInProcess : listOrder) {
            Order order = mapOrderInProcess.get(orderInProcess.getId());
            if (order != null) {
                return new ResponseEntity<>(new ResponseMsg("Đơn hàng của bạn đã hoàn thành"), HttpStatus.NOT_FOUND);
            }

            int status;
            try {
                if (order.getShipper() == null || order.getShipper().isEmpty()) {
                    order.setShipper(orderInProcess.getShipper());
                }

                String[] location = ShipperController.mapLocationAvailableShipper.remove(order.getShipper());
                ShipperController.mapLocationInProgressShipper.put(order.getShipper(), location);

                if (!order.getStatus().matches("2\\d")) {
                    status = StatusController.mapStatus.keySet()
                            .stream()
                            .filter(t -> String.valueOf(t).matches("2\\d"))
                            .sorted()
                            .findFirst()
                            .orElse(21);
                } else {
                    status = Integer.parseInt(order.getStatus()) + 1;
                }
                order.setStatus(String.valueOf(status));

                service.updatetOrder(order);
                listResult.add(order);

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
        return new ResponseEntity<>(listResult, HttpStatus.OK);
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
                order.setStatus("-" + order.getStatus());
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
                int totalMinuteCurrent = DateTimeHelper.parseTimeToMinute(LocalTime.now(ZoneId.of("GMT+7")));
                if (totalMinuteCurrent >= order.getValue() - 180 && totalMinuteCurrent <= order.getValue()) {
                    mapOrderInProcess.put(order.getKey().getId(), order.getKey());
                    tmp.add(order.getKey());
                }
            }

            for (Order order : tmp) {
                OrderController.mapOrderInQueue.remove(order);
            }
        }

        String generateId(Order obj) {
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
            if (mapOrderInQueue == null) {
                mapOrderInQueue = new HashMap<>();
            }
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
                    String sql = "INSERT INTO ORDERS (ID, CUST, MALL, CREATED_DATE, CREATED_TIME, LAST_UPDATE, STATUS, NOTE, \n"
                            + "COST_SHOPPING, COST_DELIVERY, TOTAL_COST, DATE_DELIVERY, TIME_DELIVERY, \n"
                            + "LAT, LNG)\n"
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, order.getId());
                    stmt.setString(2, order.getCust());
                    stmt.setString(3, order.getMarket());
                    stmt.setDate(4, order.getCreateDate());
                    stmt.setTime(5, order.getCreateTime());
                    stmt.setTime(6, order.getLastUpdate());
                    stmt.setInt(7, Integer.parseInt(order.getStatus())); //status
                    stmt.setString(8, order.getNote());
                    stmt.setDouble(9, order.getCostShopping());
                    stmt.setDouble(10, order.getCostDelivery());
                    stmt.setDouble(11, order.getTotalCost());
                    stmt.setDate(12, order.getDateDelivery());
                    stmt.setTime(13, order.getTimeDelivery());
                    stmt.setString(14, order.getLat());
                    stmt.setString(15, order.getLng());
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

        String updatetOrder(Order order) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {

                    String sql = "UPDATE ORDERS SET SHIPEPR = ?, STATUS = ?\n"
                            + "WHERE ID = ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, order.getShipper());
                    stmt.setInt(2, Integer.parseInt(order.getStatus()));
                    stmt.setString(3, order.getId());
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

                    String sql = "UPDATE ORDERS SET STATUS = ?, AUTHOR = ?\n"
                            + "WHERE ID = ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setInt(1, Integer.parseInt(order.getStatus()));
                    stmt.setString(2, order.getAuthor());
                    stmt.setString(3, order.getId());
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
    }
}
