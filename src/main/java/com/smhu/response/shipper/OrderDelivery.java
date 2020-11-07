package com.smhu.response.shipper;

import com.smhu.controller.MarketController;
import com.google.gson.annotations.SerializedName;
import com.smhu.order.Order;

public class OrderDelivery {

    @SerializedName("destination")
    private String destination; // address delivery

    @SerializedName("value")
    private int value; // int 14300

    @SerializedName("order")
    private OrderShipper order;

    public OrderDelivery() {
    }

    public OrderDelivery(String destination, int value, Order order) {
        this.destination = destination;
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
                order.getDetails());
    }

    public String getDestination() {
        return destination;
    }

    public int getValue() {
        return value;
    }

    public OrderShipper getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "OrderDelivery{" + "destination=" + destination + ", value=" + value + ", order=" + order + '}';
    }
}
