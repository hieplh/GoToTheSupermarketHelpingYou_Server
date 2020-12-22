package com.smhu.controller;

import com.smhu.account.Shipper;
import com.smhu.dao.AccountDAO;
import com.smhu.dao.FoodDAO;
import com.smhu.history.History;
import com.smhu.iface.IStatus;
import com.smhu.order.Order;
import com.smhu.order.OrderDetail;
import com.smhu.response.ResponseMsg;
import com.smhu.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class HistoryController {

    HistoryService service;

    @GetMapping("/histories/{type}/{id}/page/{page}")
    public ResponseEntity<?> getHistoriesById(@PathVariable("id") String id, @PathVariable("type") String type,
            @PathVariable("page") String page) {
        try {
            service = new HistoryService();
            IStatus statusListener = new StatusController().getStatusListener();
            return new ResponseEntity<>(service.getOrderHistoriesById(id, type,
                    statusListener.getStatusIsDoneOrder(), service.convertPageToIndex(page)),
                    HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(HistoryController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping("/history/{orderId}")
    public ResponseEntity<?> getHistoryDetailsById(@PathVariable("orderId") String orderId) {
        try {
            service = new HistoryService();
            return new ResponseEntity<>(service.getOrderDetailsHistoryById(orderId), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(HistoryController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.OK);
        }
    }

    class HistoryService {

        final String CUSTOMER = "CUSTOMER";
        final String SHIPPER = "SHIPPER";

        final int ROWS = 20;

        private int convertPageToIndex(String page) {
            int tmp = Integer.parseInt(page);
            return tmp > 0 ? (tmp - 1) * ROWS : 0;
        }

        List<History> getOrderHistoriesById(String id, String type, int status, int page) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            List<History> list = null;
            AccountDAO dao = new AccountDAO();

            try {
                con = DBUtils.getConnection();
                if (con != null) {

                    String sql;
                    switch (type.toUpperCase()) {
                        case CUSTOMER:
                            sql = "EXEC GET_HISTORY_CUSTOMER_BY_ID ?, ?, ?";
                            break;
                        case SHIPPER:
                            sql = "EXEC GET_HISTORY_SHIPPER_BY_ID ?, ?, ?";
                            break;
                        default:
                            return null;
                    }
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, id);
                    stmt.setInt(2, page);
                    stmt.setInt(3, ROWS);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        History history = new History();
                        history.setId(rs.getString("ORDER_ID"));
                        history.setCreateDate(rs.getDate("CREATED_DATE"));
                        history.setCreateTime(rs.getTime("CREATED_TIME"));
                        history.setReceiveTime(rs.getTime("TIME_DELIVERY"));
                        history.setDeliveryTime(rs.getTime("LAST_UPDATE"));
                        history.setAddressDelivery(rs.getString("ADDRESS_DELIVERY"));
                        history.setShipper((Shipper) dao.getAccountById(rs.getString("SHIPPER"), "shipper"));
                        history.setMarket(MarketController.mapMarket.get(rs.getString("MARKET_ID")));
                        history.setStatus(rs.getInt("STATUS"));
                        history.setNote(rs.getString("NOTE"));
                        history.setCostDelivery(rs.getDouble("COST_DELIVERY"));
                        history.setCostShopping(rs.getDouble("COST_SHOPPING"));
                        history.setTotalCost(rs.getDouble("TOTAL_COST"));
                        list.add(history);
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
            return list;
        }

        private Order loadOrder(String orderId) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT MARKET\n"
                            + "FROM ORDERS\n"
                            + "WHERE ID = ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, orderId);

                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        Order order = new Order();
                        order.setMarket(MarketController.mapMarket.get(rs.getString("MARKET")));
                        return order;
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
            return null;
        }

        List<OrderDetail> getOrderDetailsHistoryById(String orderId) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            List<OrderDetail> listDetails = null;
            FoodDAO dao = new FoodDAO();

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    Order order = loadOrder(orderId);
                    if (order == null) {
                        return null;
                    }

                    String sql = "EXEC GET_ORDER_DETAIL_BY_ID ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, orderId);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        if (listDetails == null) {
                            listDetails = new ArrayList<>();
                        }
                        listDetails.add(new OrderDetail(rs.getString("ID"),
                                dao.getFoodById(order.getMarket().getId(), rs.getString("FOOD")),
                                rs.getDouble("ORIGINAL_PRICE"),
                                rs.getDouble("PAID_PRICE"),
                                rs.getDouble("WEIGHT"),
                                rs.getInt("SALE_OFF")));
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
            return listDetails;
        }
    }
}
