package com.capstone.order;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Order {

    private String id;
    private String cust;
    private String shipper;
    private String mall;
    private Date createDate;
    private Time createTime;
    private Time lastUpdate;
    private int status;
    private String note;
    private double costShopping;
    private double costDelivery;
    private double totalCost;
    private Date dateDelivery;
    private Time timeDelivery;
    private TimeTravel timeTravel;
    private Evidence evidence;

    public Order() {
    }

    public Order(String id, String cust, String mall, Date createDate, Time createTime, Time lastUpdate, int status, String note,
            double costShopping, double costDelivery, double totalCost, Date dateDelivery, Time timeDelivery, TimeTravel timeTravel) {
        this.id = id;
        this.cust = cust;
        this.mall = mall;
        this.createDate = createDate;
        this.createTime = createTime;
        this.lastUpdate = lastUpdate;
        this.status = status;
        this.note = note;
        this.costShopping = costShopping;
        this.costDelivery = costDelivery;
        this.totalCost = totalCost;
        this.dateDelivery = dateDelivery;
        this.timeDelivery = timeDelivery;
        this.timeTravel = timeTravel;
    }

    public String getId() {
        return id;
    }

    public String getCust() {
        return cust;
    }

    public String getShipper() {
        return shipper;
    }

    public String getMall() {
        return mall;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Time getLastUpdate() {
        return lastUpdate;
    }

    public int getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public double getCostShopping() {
        return costShopping;
    }

    public double getCostDelivery() {
        return costDelivery;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public Date getDateDelivery() {
        return dateDelivery;
    }

    public Time getTimeDelivery() {
        return timeDelivery;
    }

    public TimeTravel getTimeTravel() {
        return timeTravel;
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public void setCust(String cust) {
        this.cust = cust;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public void setMall(String mall) {
        this.mall = mall;
    }

    public void setLastUpdate(Time lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setCostShopping(double costShopping) {
        this.costShopping = costShopping;
    }

    public void setCostDelivery(double costDelivery) {
        this.costDelivery = costDelivery;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void setDateDelivery(Date dateDelivery) {
        this.dateDelivery = dateDelivery;
    }

    public void setTimeDelivery(Time timeDelivery) {
        this.timeDelivery = timeDelivery;
    }

    public void setTimeTravel(TimeTravel timeTravel) {
        this.timeTravel = timeTravel;
    }

    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
    }

    public Time getCreateTime() {
        return createTime;
    }
}