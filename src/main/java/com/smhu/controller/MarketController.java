package com.smhu.controller;

import com.smhu.market.Market;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MarketController {

    public static Map<String, Market> mapMarket = new HashMap<>();

    MarketService service = new MarketService();

    @GetMapping("/markets")
    public ResponseEntity<?> getMarkets() {
        try {
            if (mapMarket.isEmpty()) {
                List<Market> markets = service.getMarkets();
                for (Market market : markets) {
                    MarketController.mapMarket.put(market.getId(), market);
                }
            }
            return new ResponseEntity<>(mapMarket.values(), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(MarketController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    class MarketService {

        List<Market> getMarkets() throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            List<Market> listMarkets = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT *\n"
                            + "FROM GET_ALL_MARKETS";
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
                                rs.getString("LNG")));
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
    }
}
