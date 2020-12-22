package com.smhu.dao;

import com.smhu.account.Account;
import com.smhu.account.Shipper;
import com.smhu.account.ShipperAlter;
import com.smhu.controller.MarketController;
import com.smhu.iface.IAccount;
import com.smhu.iface.IOrder;
import com.smhu.order.Order;
import com.smhu.order.OrderDetail;
import com.smhu.statement.QueryStatement;
import com.smhu.utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDAO implements IOrder {

    public final String STAFF = "STAFF";
    public final String CUSTOMER = "CUSTOMER";
    public final String SHIPPER = "SHIPPER";

    @Override
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
                    order.setMarket(MarketController.mapMarket.get(rs.getString("MARKET")));
                    AccountDAO dao = new AccountDAO();
                    order.setShipper((Shipper) dao.getAccountById(rs.getString("SHIPPER"), "shipper"));
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

    @Override
    public List<ShipperAlter> getListShipperAlter(String orderId) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        IAccount accountListener = new AccountDAO();
        List<ShipperAlter> list = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "SELECT *\n"
                        + "FROM SHIPPER_ALTER\n"
                        + "WHERE DH = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, orderId);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(new ShipperAlter(rs.getString("ID"),
                            (Shipper) accountListener.getAccountById(rs.getString("ORIGINAL"), SHIPPER),
                            (Shipper) accountListener.getAccountById(rs.getString("ALTERNATIVE"), SHIPPER),
                            (Account) accountListener.getAccountById(rs.getString("AUTHOR"), STAFF),
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
    public String insertAlterShipper(String orderId, String oldShipper, String newShipper, String author) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "INSERT INTO SHIPPER_ALTER (ID, ORIGINAL, ALTERNATIVE, CREATE_DATE, CREATE_TIME, AUTHOR, DH)\n"
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, author + orderId);
                stmt.setString(2, oldShipper);
                stmt.setString(3, newShipper);
                stmt.setDate(4, new java.sql.Date(new Date().getTime()));
                stmt.setTime(5, new Time(new Date().getTime()));
                stmt.setString(6, author);
                stmt.setString(7, orderId);
                return stmt.executeUpdate() > 0 ? author + orderId : null;
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

    @Override
    public String insertOrder(Order order) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.insertOrder;
                stmt = con.prepareStatement(sql);
                stmt.setString(1, order.getId());
                stmt.setString(2, order.getCust());
                stmt.setString(3, order.getAddressDelivery());
                stmt.setString(4, order.getMarket().getId());
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

    @Override
    public int[] insertOrderDetail(String idOrder, List<OrderDetail> details) throws SQLException, ClassNotFoundException {
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
                    stmt.setString(2, detail.getFood().getId());
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

    @Override
    public String updatetOrder(Order order) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {

                String sql = "UPDATE ORDERS SET SHIPPER = ?, STATUS = ?, LAT = ?, LNG = ?, LAST_UPDATE = ?\n"
                        + "WHERE ID = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, order.getShipper().getUsername());
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

    @Override
    public String CancelOrder(Order order) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {

                String sql = "UPDATE ORDERS SET STATUS = ?, AUTHOR = ?, REASON_CANCEL = ?, REFUND_COST = ?, LAST_UPDATE = ?\n"
                        + "WHERE ID = ?";
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, order.getStatus());
                stmt.setString(2, order.getAuthor());
                stmt.setString(3, order.getReasonCancel());
                stmt.setDouble(4, order.getRefundCost());
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
}
