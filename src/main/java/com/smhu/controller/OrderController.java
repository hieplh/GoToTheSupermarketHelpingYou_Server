package com.smhu.controller;

import com.smhu.helper.DateTimeHelper;
import com.smhu.helper.GsonHelper;
import com.smhu.iface.IOrder;
import com.smhu.msg.ErrorMsg;
import com.smhu.order.Order;
import com.smhu.order.OrderDetail;
import com.smhu.utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {

    public static Map<String, Order> mapOrders = new HashMap<>();
    public static Map<Order, Integer> mapOrderInQueue = new HashMap<>();
    public static List<Order> listOrderInProcess = new ArrayList<>();

    IOrder orderListener = new OrderService();

    OrderService service = new OrderService();

    @GetMapping("/testorder")
    public ResponseEntity test() {
        service.checkOrderInqueue();
        return new ResponseEntity(GsonHelper.gson.toJson(listOrderInProcess), HttpStatus.OK);
    }
    
    @PostMapping("/orders")
    public ResponseEntity newOrder(@RequestBody Order obj) {
        try {
            Order order = new Order(new OrderService().generateId(obj),
                    obj.getCust(),
                    obj.getMarket(),
                    new java.sql.Date(new Date().getTime()),
                    new Time(new Date().getTime()),
                    new Time(new Date().getTime()),
                    "12",
                    obj.getNote(),
                    obj.getCostShopping(),
                    obj.getCostDelivery(),
                    obj.getTotalCost(),
                    obj.getDateDelivery(),
                    obj.getTimeDelivery(),
                    obj.getTimeTravel(),
                    obj.getDetails());

            String result = service.insertOrder(order);

            if (result == null) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (service.insertOrderDetail(result, order.getDetails()) == null) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            service.addOrderInqueue(obj);
            service.checkOrderInqueue();
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ErrorMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}/shipper/{shipperId}")
    public ResponseEntity updatetOrder(@PathVariable("orderId") String orderId, @PathVariable("shipperId") String shipperId) {
        Order order = mapOrders.get(orderId);
        order.setShipper(shipperId);
        order.setStatus("21");
        mapOrders.put(orderId, order);

        try {
            service.updatetOrder(order);
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ErrorMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    public IOrder getOrderListener() {
        return orderListener;
    }

    class OrderService implements IOrder {

        @Override
        public void checkOrderInqueue() {
            for (Map.Entry<Order, Integer> order : OrderController.mapOrderInQueue.entrySet()) {
                System.out.println("Local Time: " + LocalTime.now(ZoneId.of("GMT+7")).toString());
                int totalMinuteCurrent = DateTimeHelper.parseTimeToMinute(LocalTime.now(ZoneId.of("GMT+7")));
                if (totalMinuteCurrent >= order.getValue() - 15 && totalMinuteCurrent <= order.getValue()) {
                    OrderController.listOrderInProcess.add(order.getKey());
                }
            }
        }

        String generateId(Order obj) {
            return obj.getCust()
                    + String.valueOf(LocalTime.now(ZoneId.of("GMT+7")).getHour())
                    + String.valueOf(LocalTime.now(ZoneId.of("GMT+7")).getMinute())
                    + String.valueOf(LocalTime.now(ZoneId.of("GMT+7")).getSecond());
        }

        void addOrderInMapOrders(Order order) {
            mapOrders.put(order.getId(), order);
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
                            + "COST_SHOPPING, COST_DELIVERY, TOTAL_COST, DATE_DELIVERY, TIME_DELIVERY)\n"
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, order.getId());
                    stmt.setString(2, order.getCust());
                    stmt.setString(3, order.getMarket());
                    stmt.setDate(4, order.getCreateDate());
                    stmt.setTime(5, order.getCreateTime());
                    stmt.setTime(6, order.getLastUpdate());
                    stmt.setInt(7, 10);
                    stmt.setString(8, order.getNote());
                    stmt.setDouble(9, order.getCostShopping());
                    stmt.setDouble(10, order.getCostDelivery());
                    stmt.setDouble(11, order.getTotalCost());
                    stmt.setDate(12, order.getDateDelivery());
                    stmt.setTime(13, order.getTimeDelivery());
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
                    String sql = "UPDATE ORDERS SET SHIPPER = ?, STATUS = ?\n"
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
    }
}
