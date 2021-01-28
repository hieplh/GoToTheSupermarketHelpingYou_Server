package com.smhu.dao;

import com.smhu.food.Category;
import com.smhu.iface.IMarket;
import com.smhu.market.Market;
import com.smhu.statement.QueryStatement;
import com.smhu.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketDAO implements IMarket {

    @Override
    public List<Market> getBranchMarkets() throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Market> listMarkets = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectMarket;
                stmt = con.prepareStatement(sql);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (listMarkets == null) {
                        listMarkets = new ArrayList<>();
                    }
                    listMarkets.add(new Market(rs.getString("ID"),
                            rs.getString("NAME"),
                            rs.getString("STREET"),
                            rs.getString("WARD"),
                            rs.getString("DISTRICT"),
                            rs.getString("PROVINCE"),
                            rs.getString("LAT"),
                            rs.getString("LNG"),
                            rs.getString("IMAGE")));
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
        return listMarkets;
    }

    public List<Market> getBranchMarkets(String corporationId) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Market> listMarkets = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectMarket
                        + "WHERE BRANCH = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, corporationId);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (listMarkets == null) {
                        listMarkets = new ArrayList<>();
                    }
                    listMarkets.add(new Market(rs.getString("ID"),
                            rs.getString("NAME"),
                            rs.getString("STREET"),
                            rs.getString("WARD"),
                            rs.getString("DISTRICT"),
                            rs.getString("PROVINCE"),
                            rs.getString("LAT"),
                            rs.getString("LNG"),
                            rs.getString("IMAGE")));
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
        return listMarkets;
    }

    public List<Map<String, String>> getCorporations() throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Map<String, String>> list = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectCorporation;
                stmt = con.prepareStatement(sql);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    Map<String, String> map = new HashMap<>();
                    map.put("image", rs.getString("IMAGE"));
                    map.put("name", rs.getString("NAME"));
                    map.put("id", rs.getString("ID"));
                    list.add(map);
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
    public Market getMarketById(String id) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectMarket
                        + "WHERE ID = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, id);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Market(rs.getString("ID"),
                            rs.getString("NAME"),
                            rs.getString("STREET"),
                            rs.getString("WARD"),
                            rs.getString("DISTRICT"),
                            rs.getString("PROVINCE"),
                            rs.getString("LAT"),
                            rs.getString("LNG"),
                            rs.getString("IMAGE"));
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
    public List<Category> getCategoryByMarketId(String id) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Category> list = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectCategory
                        + "WHERE M.ID = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, id);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (list == null) {
                        list = new ArrayList();
                    }
                    list.add(new Category(
                            rs.getString("CATEGORY_ID"),
                            rs.getString("CATEGORY_DESC"),
                            null));
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
