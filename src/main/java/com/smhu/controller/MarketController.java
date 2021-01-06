package com.smhu.controller;

import com.smhu.dao.MarketDAO;
import com.smhu.market.Market;
import com.smhu.response.ResponseMsg;
import java.sql.SQLException;
import java.util.HashMap;
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
@RequestMapping("/api/smhu")
public class MarketController {

    public static Map<String, Market> mapMarket = new HashMap<>();

    MarketDAO service;

    public MarketController() {
        service = new MarketDAO();
    }

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
}
