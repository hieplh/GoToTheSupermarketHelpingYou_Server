package com.smhu.response.shipper;

import com.smhu.controller.MarketController;
import com.google.gson.annotations.SerializedName;
import com.smhu.order.Order;

public class OrderDelivery {

    @SerializedName("distance")
    private String distance; // string 14.3 km

    @SerializedName("value")
    private int value; // int 14300

    @SerializedName("order")
    private OrderShipper order;

    public OrderDelivery() {
    }

    public OrderDelivery(String distance, int value, Order order) {
        this.distance = distance;
        this.value = value;
        this.order = new OrderShipper(order.getId(),
                order.getCust(),
                order.getAddressDelivery(),
                MarketController.mapMarket.get(order.getMarket()),
                order.getNote(),
                order.getShipper(),
                order.getStatus(),
                order.getCostShopping(),
                order.getCostDelivery(),
                order.getTotalCost(),
                order.getDateDelivery(),
                order.getTimeDelivery(),
                order.getDetails(),
                null, null);
    }

    public String getDistance() {
        return distance;
    }

    public int getValue() {
        return value;
    }

    public OrderShipper getOrder() {
        return order;
    }
}
