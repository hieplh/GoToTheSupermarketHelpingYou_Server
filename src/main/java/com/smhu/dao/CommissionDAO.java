package com.smhu.dao;

import com.smhu.commission.Commission;
import com.smhu.commission.TemporaryEvent;
import com.smhu.statement.QueryStatement;
import com.smhu.utils.DBUtils;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class CommissionDAO {
    
    public Commission getCommission(Date curDate, Time curTime) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectCommission;
                stmt = con.prepareStatement(sql);
                stmt.setDate(1, curDate);
                stmt.setTime(2, curTime);
                stmt.setDate(3, curDate);
                stmt.setTime(4, curTime);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Commission(rs.getString("ID"),
                            rs.getDate("CREATED_DATE"),
                            rs.getTime("CREATED_TIME"),
                            rs.getDate("APPLY_DATE"),
                            rs.getTime("APPLY_TIME"),
                            rs.getInt("FIRST_SHIPPING"),
                            rs.getInt("FIRST_SHOPPING"),
                            rs.getTime("MORNING_TIME"),
                            rs.getDouble("FSI_MOR_COST"),
                            rs.getDouble("NSI_MOR_COST"),
                            rs.getDouble("FSO_MOR_COST"),
                            rs.getDouble("NSO_MOR_COST"),
                            rs.getTime("MIDDAY_TIME"),
                            rs.getDouble("FSI_MID_COST"),
                            rs.getDouble("NSI_MID_COST"),
                            rs.getDouble("FSO_MID_COST"),
                            rs.getDouble("NSO_MID_COST"),
                            rs.getTime("AFTERNOON_TIME"),
                            rs.getDouble("FSI_AF_COST"),
                            rs.getDouble("NSI_AF_COST"),
                            rs.getDouble("FSO_AF_COST"),
                            rs.getDouble("NSO_AF_COST"),
                            rs.getTime("EVENING_TIME"),
                            rs.getDouble("FSI_EVE_COST"),
                            rs.getDouble("NSI_EVE_COST"),
                            rs.getDouble("FSO_EVE_COST"),
                            rs.getDouble("NSO_EVE_COST"),
                            rs.getInt("SHIPPING_COMMISSION"),
                            rs.getInt("SHOPPING_COMMISSION"));
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
    
    public List<TemporaryEvent> getCommissionEvent(Date curDate) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<TemporaryEvent> list = null;
        
        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectCommissionEvent;
                stmt = con.prepareStatement(sql);
                stmt.setDate(1, curDate);
                stmt.setDate(2, curDate);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(new TemporaryEvent(rs.getString("ID"),
                            rs.getString("DESCRIPTION"),
                            rs.getDate("CREATED_DATE"),
                            rs.getTime("CREATED_TIME"),
                            rs.getDate("START_DATE"),
                            rs.getDate("END_DATE"),
                            rs.getTime("START_TIME"),
                            rs.getTime("END_TIME"),
                            rs.getDouble("BONUS_SHIPPING_COST"),
                            rs.getDouble("BONUS_SHOPPING_COST")));
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
