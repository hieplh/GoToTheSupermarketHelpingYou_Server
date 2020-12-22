package com.smhu.dao;

import com.smhu.order.Evidence;
import com.smhu.utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ImageDAO {

    private String generateId(String s) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            sb.append((char) (97 + random.nextInt(25)));
        }
        s = "I_" + s + sb.toString();
        return s;
    }

    public List<Evidence> getEvidencesByOrderId(String orderId) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Evidence> list = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "EXEC GET_EVIDENCE_BY_ORDER_ID ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, orderId);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(new Evidence(
                            rs.getString("ID"),
                            rs.getString("IMAGE"),
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

    public int insertEvidence(String imagePath, String orderId) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "INSERT INTO EVIDENCE (ID, IMAGE, CREATE_DATE, CREATE_TIME, DH)\n"
                        + "VALUES (?, ?, ?, ?, ?)";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, generateId(orderId));
                stmt.setString(2, imagePath);
                stmt.setDate(3, new java.sql.Date(new Date().getTime()));
                stmt.setTime(4, new Time(new Date().getTime()));
                stmt.setString(5, orderId);
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
