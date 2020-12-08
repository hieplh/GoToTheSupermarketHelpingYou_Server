package com.smhu.response.customer;

import com.smhu.account.Shipper;
import com.smhu.market.Market;
import java.sql.Time;

public class OrderResponseCustomer {

    private String id;
    private Market market;
    private int quantity;
    private double totalCost;
    private Time timeCreate;
    private Time timeDelivery;
    private int status;
    private Shipper shipper;

    public OrderResponseCustomer() {
    }

    public OrderResponseCustomer(String id, Market market, int quantity, double totalCost, Time timeCreate, Time timeDelivery, int status, Shipper shipper) {
        this.id = id;
        this.market = market;
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.timeCreate = timeCreate;
        this.timeDelivery = timeDelivery;
        this.status = status;
        this.shipper = shipper;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public Time getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Time timeCreate) {
        this.timeCreate = timeCreate;
    }

    public Time getTimeDelivery() {
        return timeDelivery;
    }

    public void setTimeDelivery(Time timeDelivery) {
        this.timeDelivery = timeDelivery;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Shipper getShipper() {
        return shipper;
    }

    public void setShipper(Shipper shipper) {
        this.shipper = shipper;
    }

    @Override
    public String toString() {
        return "OrderResponseCustomer{" + "id=" + id + ", market=" + market + ", quantity=" + quantity + ", totalCost=" + totalCost + ", timeCreate=" + timeCreate + ", timeDelivery=" + timeDelivery + ", status=" + status + ", shipper=" + shipper + '}';
    }
}
