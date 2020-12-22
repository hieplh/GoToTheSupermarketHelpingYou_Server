package com.smhu.controller;

import com.smhu.dao.FoodDAO;
import com.smhu.dao.MarketDAO;
import com.smhu.food.Category;
import java.util.ArrayList;
import java.util.List;
import com.smhu.food.Food;
import com.smhu.food.SaleOff;
import com.smhu.iface.IFood;
import com.smhu.iface.IMarket;
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
    
    IFood service = new FoodDAO();
    
    @GetMapping("/foods/{marketId}")
    public ResponseEntity<?> getAllFoodsAtMarket(@PathVariable("marketId") String id) {
        try {
            return new ResponseEntity<>(service.getAllFoodsAtMarket(id), HttpStatus.OK);
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(FoodController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/food/{marketId}/{foodId}")
    public ResponseEntity<?> getFoodById(@PathVariable("marketId") String marketId, @PathVariable("foodId") String foodId) {
        try {
            return new ResponseEntity<>(service.getFoodById(marketId, foodId), HttpStatus.OK);
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(FoodController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
