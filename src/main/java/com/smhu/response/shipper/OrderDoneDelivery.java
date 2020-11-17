package com.smhu.response.shipper;

import java.sql.Date;
import java.sql.Time;

public class OrderDoneDelivery {

    private String id;
    private String marketName;
    private int quantity;
    private double costDelivery;
    private double costShopping;
    private double totalCostItems;
    private double totalCost;
    private Date createDate;
    private Time endTime;
    private int status;
    private String note;

    public OrderDoneDelivery() {
    }

    public OrderDoneDelivery(String id, String marketName, int quantity, double costDelivery, double costShopping, double totalCostItems, double totalCost, Date createDate, Time endTime, int status, String note) {
        this.id = id;
        this.marketName = marketName;
        this.quantity = quantity;
        this.costDelivery = costDelivery;
        this.costShopping = costShopping;
        this.totalCostItems = totalCostItems;
        this.totalCost = totalCost;
        this.createDate = createDate;
        this.endTime = endTime;
        this.status = status;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getCostDelivery() {
        return costDelivery;
    }

    public void setCostDelivery(double costDelivery) {
        this.costDelivery = costDelivery;
    }

    public double getCostShopping() {
        return costShopping;
    }

    public void setCostShopping(double costShopping) {
        this.costShopping = costShopping;
    }

    public double getTotalCostItems() {
        return totalCostItems;
    }

    public void setTotalCostItems(double totalCostItems) {
        this.totalCostItems = totalCostItems;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "OrderDoneDelivery{" + "id=" + id + ", marketName=" + marketName + ", quantity=" + quantity + ", costDelivery=" + costDelivery + ", costShopping=" + costShopping + ", totalCostItems=" + totalCostItems + ", totalCost=" + totalCost + ", createDate=" + createDate + ", endTime=" + endTime + ", status=" + status + ", note=" + note + '}';
    }

}
