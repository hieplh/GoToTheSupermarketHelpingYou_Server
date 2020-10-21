package com.smhu.controller;

import com.smhu.GototheSupermarketHelpingYouApplication;
import com.smhu.history.History;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HistoryController {

    final int ROWS = 20;

    HistoryService service = new HistoryService();

    @GetMapping("/histories/cust/{id}/page/{page}")
    public ResponseEntity<?> getHistoryByCustId(@PathVariable("id") String custId, @PathVariable("page") String page) {
        try {
            return new ResponseEntity<>(service.getHistoriesOrderByCustId(custId, service.convertPageToIndex(page)), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(HistoryController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping("/history/{orderId}")
    public ResponseEntity<?> getHistoryDetails(@PathVariable("orderId") String orderId) {
        try {
            return new ResponseEntity<>(service.getHistoryOrderDetailByCustId(orderId), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(HistoryController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.OK);
        }
    }

    class HistoryService {

        private int convertPageToIndex(String page) {
            int tmp = Integer.parseInt(page);
            return tmp > 0 ? (tmp - 1) * ROWS : 0;
        }

        List<History> getHistoriesOrderByCustId(String custId, int page) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            List<History> list = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {

                    String sql = "SELECT O.ID, O.CREATED_DATE, O.CREATED_TIME, O.ADDRESS_DELIVERY, O.SHIPPER, \n"
                            + "M.NAME, O.NOTE, O.COST_DELIVERY, O.COST_SHOPPING, O.TOTAL_COST\n"
                            + "FROM ORDERS O\n"
                            + "JOIN MARKET M\n"
                            + "ON O.MARKET = M.ID\n"
                            + "WHERE CUST = ?\n"
                            + "ORDER BY CREATED_DATE DESC, CREATED_TIME DESC\n"
                            + "OFFSET " + page + " ROWS\n"
                            + "FETCH NEXT " + ROWS + " ROWS ONLY";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, custId);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        History history = new History();
                        history.setId(rs.getString("ID"));
                        history.setCreateDate(rs.getDate("CREATED_DATE"));
                        history.setCreateTime(rs.getTime("CREATED_TIME"));
                        history.setAddressDelivery(rs.getString("ADDRESS_DELIVERY"));
                        history.setShipper(rs.getString("SHIPPER"));
                        history.setMarketName(rs.getString("NAME"));
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

        List<OrderDetail> getHistoryOrderDetailByCustId(String orderId) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            List<OrderDetail> listDetails = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "EXEC GET_ORDER_DETAIL_BY_ID ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, orderId);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        if (listDetails == null) {
                            listDetails = new ArrayList<>();
                        }
                        listDetails.add(new OrderDetail(rs.getString("ID"),
                                rs.getString("NAME"),
                                rs.getString("IMAGE"),
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
