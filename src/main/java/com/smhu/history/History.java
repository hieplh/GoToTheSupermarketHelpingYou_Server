package com.smhu.history;

import com.smhu.account.Shipper;
import com.smhu.market.Market;
import java.sql.Date;
import java.sql.Time;

public class History {

    private String id;
    private String addressDelivery;
    private Market market;
    private String note;
    private Shipper shipper;
    private int status;
    private Date createDate;
    private Time createTime;
    private Time receiveTime;
    private Time deliveryTime;
    private double costShopping;
    private double costDelivery;
    private double totalCost;

    public History() {
    }

    public History(String id, String addressDelivery, Market market, String note, Shipper shipper, int status, Date createDate, Time createTime, Time receiveTime, Time deliveryTime, double costShopping, double costDelivery, double totalCost) {
        this.id = id;
        this.addressDelivery = addressDelivery;
        this.market = market;
        this.note = note;
        this.shipper = shipper;
        this.status = status;
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

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Shipper getShipper() {
        return shipper;
    }

    public void setShipper(Shipper shipper) {
        this.shipper = shipper;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "History{" + "id=" + id + ", addressDelivery=" + addressDelivery + ", market=" + market + ", note=" + note + ", shipper=" + shipper + ", status=" + status + ", createDate=" + createDate + ", createTime=" + createTime + ", receiveTime=" + receiveTime + ", deliveryTime=" + deliveryTime + ", costShopping=" + costShopping + ", costDelivery=" + costDelivery + ", totalCost=" + totalCost + '}';
    }

}
