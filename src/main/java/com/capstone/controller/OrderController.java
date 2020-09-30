package com.capstone.controller;

import com.capstone.msg.ErrorMsg;
import com.capstone.order.Order;
import com.capstone.order.OrderDetail;
import com.capstone.utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {

    @PostMapping("/orders")
    public ResponseEntity newOrder(@RequestBody Order obj) {
        try {
            Order order = new Order(new OrderService().generateId(obj),
                    obj.getCust(), obj.getMall(),
                    new java.sql.Date(new Date().getTime()), new Time(new Date().getTime()),
                    new Time(new Date().getTime()), 1, obj.getNote(),
                    obj.getCostShopping(), obj.getCostDelivery(), obj.getTotalCost(),
                    obj.getDateDelivery(), obj.getTimeDelivery(), obj.getTimeTravel(),
                    obj.getDetail());

            String result = new OrderService().insertOrder(order);

            if (result == null) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (new OrderService().insertOrderDetail(result, order.getDetail()) == null) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ErrorMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    class OrderService {

        String generateId(Order obj) {
            return obj.getCust()
                    + String.valueOf(LocalTime.now(ZoneId.of("GMT+7")).getHour())
                    + String.valueOf(LocalTime.now(ZoneId.of("GMT+7")).getMinute())
                    + String.valueOf(LocalTime.now(ZoneId.of("GMT+7")).getSecond());
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
                    stmt.setString(3, order.getMall());
                    stmt.setDate(4, order.getCreateDate());
                    stmt.setTime(5, order.getCreateTime());
                    stmt.setTime(6, order.getLastUpdate());
                    stmt.setInt(7, order.getStatus());
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
                        stmt.setDouble(3, detail.getPriceOrginal());
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
    }
}
