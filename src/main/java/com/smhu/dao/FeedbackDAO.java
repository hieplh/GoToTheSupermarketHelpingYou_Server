package com.smhu.dao;

import com.smhu.feedback.Feedback;
import com.smhu.iface.IFeedback;
import com.smhu.order.Order;
import com.smhu.statement.QueryStatement;
import com.smhu.utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedbackDAO implements IFeedback {

    private String generateId(String orderId) {
        return "Fb-" + orderId;
    }

    @Override
    public Feedback getFeedbackById(String orderId) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectFeedbackById;
                stmt = con.prepareStatement(sql);
                stmt.setString(1, orderId);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Feedback(rs.getString("ID"),
                            rs.getString("CUSTOMER"),
                            rs.getString("SHIPPER"),
                            rs.getString("DH"),
                            rs.getString("FEEDBACK"),
                            rs.getInt("RATING"));
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

    @Override
    public int feedbackOrder(Order order, String content, int rating) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.feedbackOrder;
                stmt = con.prepareStatement(sql);
                stmt.setString(1, generateId(order.getId()));
                stmt.setString(2, order.getCust());
                stmt.setString(3, order.getShipper().getUsername());
                stmt.setString(4, order.getId());
                stmt.setString(5, content);
                stmt.setInt(6, rating);
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
}
