//package com.smhu.dao;
//
//import com.smhu.food.Category;
//import com.smhu.food.Food;
//import com.smhu.food.SaleOff;
//import com.smhu.utils.DBUtils;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CategoryDAO {
//
//    List<Category> getAllFoodsAtMarket(String mall) throws SQLException, ClassNotFoundException {
//        Connection con = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//        List<Food> listFoods = null;
//
//        try {
//            con = DBUtils.getConnection();
//            if (con != null) {
//                String sql = "SELECT *\n"
//                        + "FROM GET_ALL_FOODS_AT_MARKET\n"
//                        + "WHERE MARKET_ID = ?";
//                stmt = con.prepareStatement(sql);
//                stmt.setString(1, mall);
//                rs = stmt.executeQuery();
//                while (rs.next()) {
//                    if (listFoods == null) {
//                        listFoods = new ArrayList<>();
//                    }
//                    listFoods.add(new Food(rs.getString("ID"),
//                            rs.getString("FOOD_NAME"),
//                            rs.getString("IMAGE"),
//                            rs.getString("DESCRIPTION"),
//                            rs.getDouble("PRICE"),
//                            new SaleOff(rs.getDate("START_DATE"),
//                                    rs.getDate("END_DATE"),
//                                    rs.getTime("START_TIME"),
//                                    rs.getTime("END_TIME"),
//                                    rs.getInt("SALE_OFF"))));
//                }
//            }
//        } finally {
//            if (rs != null) {
//                rs.close();
//            }
//            if (stmt != null) {
//                stmt.close();
//            }
//            if (con != null) {
//                con.close();
//            }
//        }
//        return listFoods;
//    }
//}
