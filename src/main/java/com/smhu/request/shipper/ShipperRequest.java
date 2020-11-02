package com.smhu.request.shipper;

import java.util.List;

public class ShipperRequest {

    private String id;
    private List<String> orders;

    public ShipperRequest() {
    }

    public ShipperRequest(String id, List<String> orders) {
        this.id = id;
        this.orders = orders;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getOrders() {
        return orders;
    }

    public void setOrders(List<String> orders) {
        this.orders = orders;
    }

}
