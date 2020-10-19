package com.smhu.response;

import com.smhu.controller.MarketController;
import com.google.gson.annotations.SerializedName;

public class OrderDelivery {

    @SerializedName("distance")
    private String distance; // string 14.3 dam

    @SerializedName("value")
    private int value; // int 14300

    @SerializedName("order")
    private OrderObj order; 

    public OrderDelivery() {
    }

    public OrderDelivery(String distance, int value, com.smhu.order.Order order) {
        this.distance = distance;
        this.value = value;
        this.order = new OrderObj(order.getId(),
                order.getCust(),
                MarketController.mapMarket.get(order.getMarket()),
                order.getNote(),
                order.getShipper(),
                Integer.parseInt(order.getStatus()),
                order.getCostShopping(),
                order.getCostDelivery(),
                order.getTotalCost(),
                order.getDateDelivery(),
                order.getTimeDelivery(),
                order.getDetails(),
                order.getLat(),
                order.getLng());
    }

    public String getDistance() {
        return distance;
    }

    public int getValue() {
        return value;
    }

    public OrderObj getOrder() {
        return order;
    }
}
