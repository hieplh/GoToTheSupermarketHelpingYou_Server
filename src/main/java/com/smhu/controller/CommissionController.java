package com.smhu.controller;

import com.smhu.commission.Commission;
import com.smhu.commission.TemporaryEvent;
import com.smhu.google.matrixobj.MatrixObject;
import com.smhu.helper.ExtractElementDistanceMatrixApi;
import com.smhu.helper.ExtractRangeInMechanism;
import com.smhu.helper.MatrixObjBuilder;
import com.smhu.market.Market;

import java.io.IOException;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class CommissionController {

    public static Commission commission;
    public static List<TemporaryEvent> listEvents;

    final CommissionService service;

    public CommissionController() {
        service = new CommissionService();
    }
    
    @GetMapping("/service/{marketId}/{destAddress}/{deliveryTime}/{quantity}")
    public ResponseEntity<?> getServiceCost(@PathVariable("marketId") String marketId, @PathVariable("destAddress") String destAddress,
            @PathVariable("deliveryTime") Time deliveryTime, @PathVariable("quantity") int quantity) {
        Market market = MarketController.mapMarket.get(marketId);
        int distance = -1;
        try {
            MatrixObject matrixObject = MatrixObjBuilder.getMatrixObject(new String[]{market.getLat(), market.getLng()}, destAddress);
            ExtractElementDistanceMatrixApi extract = new ExtractElementDistanceMatrixApi();
            List<String> distances = extract.getListDistance(extract.getListElements(matrixObject), extract.VALUE);
            for (String value : distances) {
                distance = Integer.parseInt(value);
            }
        } catch (IOException e) {
            Logger.getLogger(CommissionController.class.getName()).log(Level.SEVERE, "Get Distance Source - Destination: {0}", e.getMessage());
        }

        double resultShippingCost;
        double resultShoppingCost;
        try {
            resultShippingCost = service.calculateShippingCost(deliveryTime, distance);
            resultShoppingCost = service.calculateShoppingCost(deliveryTime, quantity);
        } catch (Exception e) {
            resultShippingCost = 15000;
            resultShoppingCost = 20000;
            Logger.getLogger(CommissionController.class.getName()).log(Level.SEVERE, "Calculate Cost: {0}", e.getMessage());
        }

        Map<String, Double> map = new HashMap<>();
        map.put("costShopping", resultShoppingCost);
        map.put("costShipping", resultShippingCost);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    class CommissionService {

        int roundedDistance(int distance) {
            return (int) Math.round(distance / 1000.0);
        }

        private double calculateCost(int first, int source, double firstCost, double nextCost) {
            double result = firstCost;
            source = source - first;
            if (source > 0) {
                for (int i = source; i > 0; i--) {
                    result += nextCost;
                }
            }
            return result;
        }

        double calculateShippingCost(Time deliveryTime, int distance) {
            int first = commission.getFirstShipping();
            distance = roundedDistance(distance) + roundedDistance(ExtractRangeInMechanism.getTheShortesRangeInMechanism());
            double firstCost;
            double nextCost;

            if (commission.getTimeEvening().compareTo(deliveryTime) <= 0) {
                firstCost = commission.getFsiEveCost();
                nextCost = commission.getNsiEveCost();
            } else if (commission.getTimeAfternoon().compareTo(deliveryTime) <= 0) {
                firstCost = commission.getFsiAfCost();
                nextCost = commission.getNsiAfCost();
            } else if (commission.getTimeMidday().compareTo(deliveryTime) <= 0) {
                firstCost = commission.getFsiMidCost();
                nextCost = commission.getNsiMidCost();
            } else if (commission.getTimeMorning().compareTo(deliveryTime) <= 0) {
                firstCost = commission.getFsiMorCost();
                nextCost = commission.getNsiMorCost();
            } else {
                return commission.getFsiMorCost();
            }
            return calculateCost(first, distance, firstCost, nextCost);
        }

        double calculateShoppingCost(Time deliveryTime, int quantity) {
            int first = commission.getFirstShopping();
            double firstCost;
            double nextCost;

            if (commission.getTimeEvening().compareTo(deliveryTime) <= 0) {
                firstCost = commission.getFsoEveCost();
                nextCost = commission.getNsoEveCost();
            } else if (commission.getTimeAfternoon().compareTo(deliveryTime) <= 0) {
                firstCost = commission.getFsoAfCost();
                nextCost = commission.getNsoAfCost();
            } else if (commission.getTimeMidday().compareTo(deliveryTime) <= 0) {
                firstCost = commission.getFsoMidCost();
                nextCost = commission.getNsoMidCost();
            } else if (commission.getTimeMorning().compareTo(deliveryTime) <= 0) {
                firstCost = commission.getFsoMorCost();
                nextCost = commission.getNsoMorCost();
            } else {
                return commission.getFsiMorCost();
            }
            return calculateCost(first, quantity, firstCost, nextCost);
        }
    }
}
