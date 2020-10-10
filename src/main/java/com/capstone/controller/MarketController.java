package com.capstone.controller;

import com.capstone.market.Market;
import com.capstone.msg.ErrorMsg;
import com.capstone.utils.DBUtils;
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

    @GetMapping("/malls")
    public ResponseEntity<?> getApiMarkets() {
        try {
            if (mapMarket.isEmpty()) {
                List<Market> markets = service.getMalls();
                for (Market market : markets) {
                    MarketController.mapMarket.put(market.getId(), market);
                }
            }

            return new ResponseEntity<>((List<Market>) mapMarket.values(), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(MarketController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ErrorMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    class MarketService {

        List<Market> getMalls() throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            List<Market> listMalls = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT ID, NAME, ADDR_1, ADDR_2, ADDR_3, ADDR_4, LAT, LNG\n"
                            + "FROM MALL";
                    stmt = con.prepareStatement(sql);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        if (listMalls == null) {
                            listMalls = new ArrayList<>();
                        }
                        listMalls.add(new Market(rs.getString("ID"),
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
            return listMalls;
        }
    }
}
