package com.smhu.helper;

import com.smhu.utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AverageTimeTravel {

    public Map<String, Integer> getAvgTimeTravel(int date, int month, int range) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, Integer> map = null;
        
        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "EXEC GET_AVG_TIME_TRAVEL_BY_DATE_IN_MONTH ?, ?, ?";
                stmt = con.prepareStatement(sql);
                
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), new Locale("vi", "vn"));
                stmt.setInt(1, cal.get(Calendar.DAY_OF_WEEK));
                stmt.setInt(2, cal.get(Calendar.MONTH) + 1);
                stmt.setInt(3, range);
                
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (map == null) {
                        map = new HashMap<>();
                    }
                    map.put(rs.getString(rs.getString("MARKET")), (int) rs.getDouble("AVG_TIME"));
                }
            }
        } finally {
            if(rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return map;
    }
    
    public int insertAvgTimeTravel(String market, String lat, String lng,
            int range, int distance, int time) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {

                String sql = "INSERT INTO AVG_TIME_TRAVEL (MARKET, LAT, LNG, DATE, MONTH, RANGE, DISTANCE, TIME)\n"
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, market);
                stmt.setString(2, lat);
                stmt.setString(3, lng);

                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), new Locale("vi", "vn"));
                stmt.setInt(4, cal.get(Calendar.DAY_OF_WEEK));
                stmt.setInt(5, cal.get(Calendar.MONTH) + 1);
                
                stmt.setInt(6, range);
                stmt.setInt(7, distance);
                stmt.setInt(8, time);
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
