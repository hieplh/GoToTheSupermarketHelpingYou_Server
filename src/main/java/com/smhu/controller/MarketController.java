package com.smhu.controller;

import com.smhu.iface.IMarket;
import com.smhu.entity.Market;
import com.smhu.response.ResponseMsg;
import com.smhu.utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MarketController {

    public static Map<String, Market> mapMarket = new HashMap<>();

    IMarket marketListener = new MarketService();
    MarketService service = new MarketService();

    @GetMapping("/corporations/all")
    public ResponseEntity<?> getCorporationOfMarkets() {
        try {
            return new ResponseEntity<>(service.getCorporations(), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(MarketController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/markets/{corporationId}")
    public ResponseEntity<?> getAllMarketsByCorporationId(@PathVariable("corporationId") String corporationId) {
        try {
            return new ResponseEntity<>(service.getBranchMarkets(corporationId), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(MarketController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/market/{id}")
    public ResponseEntity<?> getMarketById(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(service.getMarketById(id), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(MarketController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public IMarket getMarketListener() {
        return marketListener;
    }

    class MarketService implements IMarket {

        @Override
        public List<Market> getBranchMarkets() throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            List<Market> listMarkets = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT ID, NAME, ADDR_1, ADDR_2, ADDR_3, ADDR_4, LAT, LNG, IMAGE\n"
                            + "FROM MARKET";
                    stmt = con.prepareStatement(sql);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        if (listMarkets == null) {
                            listMarkets = new ArrayList<>();
                        }
                        listMarkets.add(new Market(rs.getString("ID"),
                                rs.getString("NAME"),
                                rs.getString("ADDR_1"),
                                rs.getString("ADDR_2"),
                                rs.getString("ADDR_3"),
                                rs.getString("ADDR_4"),
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
                    String sql = "SELECT ID, NAME, ADDR_1, ADDR_2, ADDR_3, ADDR_4, LAT, LNG, IMAGE\n"
                            + "FROM MARKET\n"
                            + "WHERE CORPORATION = ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, corporationId);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        if (listMarkets == null) {
                            listMarkets = new ArrayList<>();
                        }
                        listMarkets.add(new Market(rs.getString("ID"),
                                rs.getString("NAME"),
                                rs.getString("ADDR_1"),
                                rs.getString("ADDR_2"),
                                rs.getString("ADDR_3"),
                                rs.getString("ADDR_4"),
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
                    String sql = "SELECT ID, NAME, IMAGE\n"
                            + "FROM CORPORATION";
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
                    String sql = "SELECT ID, NAME, ADDR_1, ADDR_2, ADDR_3, ADDR_4, LAT, LNG, IMAGE\n"
                            + "FROM MARKET\n"
                            + "WHERE ID = ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, id);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        return new Market(rs.getString("ID"),
                                rs.getString("NAME"),
                                rs.getString("ADDR_1"),
                                rs.getString("ADDR_2"),
                                rs.getString("ADDR_3"),
                                rs.getString("ADDR_4"),
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
    }
}
