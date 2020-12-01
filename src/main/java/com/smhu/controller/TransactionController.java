package com.smhu.controller;

import com.smhu.iface.IStatus;
import com.smhu.iface.ITransaction;
import com.smhu.response.ResponseMsg;
import com.smhu.transaction.Transaction;
import com.smhu.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api")
public class TransactionController {

    private final TransactionService service;
    private final ITransaction transactionListener;

    public TransactionController() {
        this.service = new TransactionService();
        this.transactionListener = new TransactionService();
    }

    @GetMapping("/trans/{accountId}")
    public ResponseEntity<?> getAllTransactionByAccountId(@PathVariable("accountId") String authorId) {
        try {
            return new ResponseEntity<>(service.getTransaction(authorId), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(TransactionController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ITransaction getTransactionListener() {
        return transactionListener;
    }

    class TransactionService implements ITransaction {

        private final String RECHARGE = "RECHARGE";
        private final String DELIVERY = "DELIVERY";
        private final String REFUND = "REFUND";

        private String generateId(String id, int status, String type) {
            switch (type) {
                case RECHARGE:
                    LocalTime time = LocalTime.now(ZoneId.of("GMT+7"));
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), new Locale("vi", "vn"));
                    return "T" + id
                            + String.valueOf(cal.get(Calendar.YEAR))
                            + String.valueOf(cal.get(Calendar.MONTH) + 1)
                            + String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
                            + String.valueOf(time.getHour())
                            + String.valueOf(time.getMinute())
                            + String.valueOf(time.getSecond());
                case DELIVERY:
                    return "TD" + status + id;
                case REFUND:
                    return "TR" + id;
                default:
                    return null;
            }
        }

        public List<Transaction> getTransaction(String authorId) throws ClassNotFoundException, SQLException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            List<Transaction> list = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "EXEC GET_ALL_TRANSACTION_BY_AUTHOR ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, authorId);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.add(new Transaction(
                                rs.getString("ID"),
                                rs.getString("FULL_NAME"),
                                rs.getDouble("AMOUNT"),
                                rs.getString("DESCRIPTION"),
                                rs.getInt("STATUS"),
                                rs.getString("DH"),
                                rs.getDate("CREATE_DATE"),
                                rs.getTime("CREATE_TIME")));
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

        @Override
        public int updateRechargeTransaction(String authorId, double amount) throws ClassNotFoundException, SQLException {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "INSERT INTO TRANSACTIONS (ID, ACCOUNT, AMOUNT, AUTHOR, CREATE_DATE, CREATE_TIME, STATUS)\n"
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, generateId(authorId, -1, RECHARGE));
                    stmt.setString(2, authorId);
                    stmt.setDouble(3, amount);
                    stmt.setString(4, authorId);

                    java.sql.Date date = new java.sql.Date(new Date().getTime());
                    Time time = new Time(new Date().getTime());
                    stmt.setDate(5, date);
                    stmt.setTime(6, time);

                    IStatus statusListener = new StatusController().getStatusListener();
                    stmt.setInt(7, statusListener.getStatusIsRecharge());
                    return stmt.executeUpdate() > 0 ? 1 : 0;
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

        @Override
        public int updateDeliveryTransaction(String authorId, double amount, int status, String orderId) throws ClassNotFoundException, SQLException {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "INSERT INTO TRANSACTIONS (ID, ACCOUNT, AMOUNT, AUTHOR, CREATE_DATE, CREATE_TIME, STATUS, DH)\n"
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, generateId(orderId, status, DELIVERY));
                    stmt.setString(2, authorId);
                    stmt.setDouble(3, amount);
                    stmt.setString(4, authorId);

                    java.sql.Date date = new java.sql.Date(new Date().getTime());
                    Time time = new Time(new Date().getTime());
                    stmt.setDate(5, date);
                    stmt.setTime(6, time);
                    stmt.setInt(7, status);
                    stmt.setString(8, orderId);
                    return stmt.executeUpdate() > 0 ? 1 : 0;
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

        @Override
        public int updateRefundTransaction(String authorId, double amount, int status, String orderId) throws ClassNotFoundException, SQLException {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "INSERT INTO TRANSACTIONS (ID, ACCOUNT, AMOUNT, AUTHOR, CREATE_DATE, CREATE_TIME, STATUS, DH)\n"
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, generateId(orderId, status, REFUND));
                    stmt.setString(2, authorId);
                    stmt.setDouble(3, amount);
                    stmt.setString(4, authorId);

                    java.sql.Date date = new java.sql.Date(new Date().getTime());
                    Time time = new Time(new Date().getTime());
                    stmt.setDate(5, date);
                    stmt.setTime(6, time);
                    stmt.setInt(7, status);
                    stmt.setString(8, orderId);
                    return stmt.executeUpdate() > 0 ? 1 : 0;
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
    }
}
