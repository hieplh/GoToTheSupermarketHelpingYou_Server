package com.smhu.dao;

import com.smhu.food.Category;
import com.smhu.food.Food;
import com.smhu.food.SaleOff;
import com.smhu.iface.IFood;
import com.smhu.iface.IMarket;
import com.smhu.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FoodDAO implements IFood {

    @Override
    public List<Category> getAllFoodsAtMarket(String market) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Category> result = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                IMarket marketListener = new MarketDAO();
                List<Category> categories = marketListener.getCategoryByMarketId(market);
                for (Category category : categories) {
                    if (result == null) {
                        result = new ArrayList();
                    }
                    List<Food> foods = getAllFoodsAtMarketByCategory(market, category.getId());
                    if (foods != null) {
                        result.add(new Category(category.getId(), category.getDescription(), foods));
                    }
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
        return result;
    }

    @Override
    public List<Food> getAllFoodsAtMarketByCategory(String market, String category) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Food> listFoods = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "EXEC GET_FOOD_OF_MARKET_BY_CATEGORY_ID ?, ?, ?, ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, market);
                stmt.setString(2, category);

                java.sql.Date date = new java.sql.Date(new Date().getTime());
                Time time = new Time(new Date().getTime());
                stmt.setDate(3, date);
                stmt.setTime(4, time);
                
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (listFoods == null) {
                        listFoods = new ArrayList<>();
                    }
                    listFoods.add(new Food(rs.getString("ID"),
                            rs.getString("FOOD_NAME"),
                            rs.getString("IMAGE"),
                            rs.getString("DESCRIPTION"),
                            rs.getDouble("PRICE"),
                            new SaleOff(rs.getDate("START_DATE"),
                                    rs.getDate("END_DATE"),
                                    rs.getTime("START_TIME"),
                                    rs.getTime("END_TIME"),
                                    rs.getInt("SALE_OFF"))));
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
        return listFoods;
    }

    @Override
    public Food getFoodById(String marketId, String foodId) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "EXEC GET_FOOD_BY_ID ?, ?, ?, ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, marketId);
                stmt.setString(2, foodId);
                
                java.sql.Date date = new java.sql.Date(new Date().getTime());
                Time time = new Time(new Date().getTime());
                stmt.setDate(3, date);
                stmt.setTime(4, time);
                
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Food(rs.getString("ID"),
                            rs.getString("FOOD_NAME"),
                            rs.getString("IMAGE"),
                            rs.getString("DESCRIPTION"),
                            rs.getDouble("PRICE"),
                            new SaleOff(rs.getDate("START_DATE"),
                                    rs.getDate("END_DATE"),
                                    rs.getTime("START_TIME"),
                                    rs.getTime("END_TIME"),
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
        return null;
    }
}
