package com.smhu.dao;

import com.smhu.controller.StatusController;
import com.smhu.iface.IStatus;
import com.smhu.iface.ITransaction;
import com.smhu.statement.QueryStatement;
import com.smhu.statistic.Amount;
import com.smhu.statistic.NumberOfOrders;
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

public class TransactionDAO implements ITransaction {

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

    @Override
    public List<Transaction> getTransaction(String authorId) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Transaction> list = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectTransactionByAuthor;
                stmt = con.prepareStatement(sql);
                stmt.setString(1, authorId);
                stmt.setString(2, authorId);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(new Transaction(
                            rs.getString("ID"),
                            rs.getString("FULL_NAME"),
                            rs.getDouble("AMOUNT"),
                            rs.getString("NOTE"),
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
                String sql = QueryStatement.insertTransactionWithoutOrder;
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
    public int updateDeliveryTransaction(String affectedId, String authorId, double amount, int status, String orderId) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.insertTransaction;
                stmt = con.prepareStatement(sql);
                stmt.setString(1, generateId(orderId, status, DELIVERY));
                stmt.setString(2, affectedId);
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
    public int updateRefundTransaction(String affectedId, String authorId, double amount, int status, String orderId) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.insertTransaction;
                stmt = con.prepareStatement(sql);
                stmt.setString(1, generateId(orderId, status, REFUND));
                stmt.setString(2, affectedId);
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
    public NumberOfOrders getNumOfOrders(String accountId, Date date) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        IStatus status = new StatusController().getStatusListener();

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectCountOfOrdersOfShipper;
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, cal.get(Calendar.YEAR));
                stmt.setInt(2, cal.get(Calendar.MONTH) + 1);
                stmt.setString(3, accountId);
                stmt.setInt(4, cal.get(Calendar.YEAR));
                stmt.setInt(5, cal.get(Calendar.MONTH) + 1);
                stmt.setString(6, accountId);
                stmt.setInt(7, cal.get(Calendar.YEAR));
                stmt.setInt(8, cal.get(Calendar.MONTH) + 1);
                stmt.setString(9, accountId);
                stmt.setInt(10, status.getStatusIsDoneOrder());
                stmt.setString(11, accountId);
                stmt.setInt(12, cal.get(Calendar.YEAR));
                stmt.setInt(13, cal.get(Calendar.MONTH) + 1);
                stmt.setInt(14, cal.get(Calendar.YEAR));
                stmt.setInt(15, cal.get(Calendar.MONTH) + 1);
                stmt.setString(16, accountId);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    return new NumberOfOrders(rs.getInt("TOTAL_COUNT_ORDERS"),
                            rs.getInt("COUNT_REJECTED_ORDERS"),
                            rs.getInt("COUNT_NEGATIVE_ORDERS"),
                            rs.getInt("COUNT_POSITIVE_ORDERS"));
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
        return new NumberOfOrders(0, 0, 0, 0);
    }

    @Override
    public Amount getAmountStatisticOfShipper(String accountId, Date date) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        IStatus status = new StatusController().getStatusListener();

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectAmountStatisticOfShipper;
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, cal.get(Calendar.YEAR));
                stmt.setInt(2, cal.get(Calendar.MONTH) + 1);
                stmt.setString(3, accountId);
                stmt.setInt(4, status.getStatusIsDoneOrder());
                stmt.setInt(5, cal.get(Calendar.YEAR));
                stmt.setInt(6, cal.get(Calendar.MONTH) + 1);
                stmt.setString(7, accountId);
                stmt.setInt(8, cal.get(Calendar.YEAR));
                stmt.setInt(9, cal.get(Calendar.MONTH) + 1);
                stmt.setString(10, accountId);
                stmt.setInt(11, cal.get(Calendar.YEAR));
                stmt.setInt(12, cal.get(Calendar.MONTH) + 1);
                stmt.setString(13, accountId);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    return new Amount(rs.getDouble("AMOUNT_REFUND"),
                            rs.getDouble("AMOUNT_TOTAL"),
                            rs.getDouble("AMOUNT_CHARGED"),
                            rs.getDouble("AMOUNT_EARNED"));
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
        return new Amount(0, 0, 0, 0);
    }

    @Override
    public List<Transaction> getAmountDetailStatisticOfShipper(String accountId, Date date) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Transaction> list = null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectAmountDetailStatisticOfShipper;
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, cal.get(Calendar.YEAR));
                stmt.setInt(2, cal.get(Calendar.MONTH) + 1);
                stmt.setString(3, accountId);
                rs = stmt.executeQuery();

                while (rs.next()) {
                    if (list == null) {
                        list = new ArrayList();
                    }
                    Transaction trans = new Transaction();
                    trans.setCreateDate(rs.getDate("CREATE_DATE"));
                    trans.setCreateTime(rs.getTime("CREATE_TIME"));
                    trans.setAmount(rs.getDouble("AMOUNT"));
                    trans.setStatus(rs.getInt("STATUS"));
                    list.add(trans);
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
}
