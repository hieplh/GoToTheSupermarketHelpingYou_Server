package com.smhu.controller;

import java.util.ArrayList;
import java.util.List;
import com.smhu.food.Food;
import com.smhu.food.SaleOff;
import com.smhu.response.ResponseMsg;

import com.smhu.utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class FoodController {

    FoodService service = new FoodService();

    @GetMapping("/foods/{marketId}")
    public ResponseEntity<?> getAllFoodsAtMall(@PathVariable("marketId") String id) {
        try {
            return new ResponseEntity<>(service.getAllFoodsAtMall(id), HttpStatus.OK);
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(FoodController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/food/{foodId}")
    public ResponseEntity<?> getFoodById(@PathVariable("foodId") String id) {
        try {
            return new ResponseEntity<>(service.getFoodById(id), HttpStatus.OK);
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(FoodController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    class FoodService {

        List<Food> getAllFoodsAtMall(String mall) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            List<Food> listFoods = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT *\n"
                            + "FROM GET_ALL_FOODS_AT_MARKET\n"
                            + "WHERE MARKET_ID = ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, mall);
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

        Food getFoodById(String id) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT *\n"
                            + "FROM GET_FOOD_BY_ID\n"
                            + "WHERE ID = ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, id);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        return new Food(rs.getString("ID"),
                                rs.getString("NAME"),
                                rs.getString("IMAGE"),
                                rs.getString("DESCRIPTION"),
                                rs.getDouble("PRICE"));
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
