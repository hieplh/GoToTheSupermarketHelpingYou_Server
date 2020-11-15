package com.smhu.history;

import java.sql.Date;
import java.sql.Time;

public class History {

    private String id;
    private String addressDelivery;
    private String marketName;
    private String note;
    private String shipper;
    private Date createDate;
    private Time createTime;
    private Time receiveTime;
    private Time deliveryTime;
    private double costShopping;
    private double costDelivery;
    private double totalCost;

    public History() {
    }

    public History(String id, String addressDelivery, String marketName, String note, String shipper, Date createDate, Time createTime, Time receiveTime, Time deliveryTime, double costShopping, double costDelivery, double totalCost) {
        this.id = id;
        this.addressDelivery = addressDelivery;
        this.marketName = marketName;
        this.note = note;
        this.shipper = shipper;
        this.createDate = createDate;
        this.createTime = createTime;
        this.receiveTime = receiveTime;
        this.deliveryTime = deliveryTime;
        this.costShopping = costShopping;
        this.costDelivery = costDelivery;
        this.totalCost = totalCost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddressDelivery() {
        return addressDelivery;
    }

    public void setAddressDelivery(String addressDelivery) {
        this.addressDelivery = addressDelivery;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Time getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Time createTime) {
        this.createTime = createTime;
    }

    public double getCostShopping() {
        return costShopping;
    }

    public void setCostShopping(double costShopping) {
        this.costShopping = costShopping;
    }

    public double getCostDelivery() {
        return costDelivery;
    }

    public void setCostDelivery(double costDelivery) {
        this.costDelivery = costDelivery;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public Time getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Time receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Time getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Time deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

}
