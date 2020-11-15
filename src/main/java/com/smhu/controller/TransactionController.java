package com.smhu.controller;

import com.smhu.iface.IStatus;
import com.smhu.iface.ITransaction;
import com.smhu.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

import java.time.LocalTime;
import java.time.ZoneId;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TransactionController {

    private final ITransaction transactionListener;

    public TransactionController() {
        this.transactionListener = new TransactionService();
    }

    public ITransaction getTransactionListener() {
        return transactionListener;
    }

    class TransactionService implements ITransaction {

        private String generateId(String authorId) {
            LocalTime time = LocalTime.now(ZoneId.of("GMT+7"));
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), new Locale("vi", "vn"));
            return "T" + authorId
                    + String.valueOf(cal.get(Calendar.YEAR))
                    + String.valueOf(cal.get(Calendar.MONTH) + 1)
                    + String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
                    + String.valueOf(time.getHour())
                    + String.valueOf(time.getMinute())
                    + String.valueOf(time.getSecond());
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
                    stmt.setString(1, generateId(authorId));
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
                    String sql = "INSERT INTO TRANSACTIONS (ID, ACCOUNT, AMOUNT, AUTHOR, CREATE_DATE, CREATE_TIME, STATUS, NOTE, DH)\n"
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, generateId(authorId));
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
