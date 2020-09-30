package com.capstone.order;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class Order {

    private String id; //server
    private String cust; //cust
    private String shipper; //shipper
    private String mall; //cust
    private Date createDate; //server
    private Time createTime; //server
    private Time lastUpdate; //server
    private int status; //server
    private String note; //cust
    private double costShopping; //cust
    private double costDelivery; //cust
    private double totalCost; //cust
    private Date dateDelivery; //cust
    private Time timeDelivery; //cust
    private TimeTravel timeTravel; //cust
    private Evidence evidence; //shipper
    private List<OrderDetail> detail;

    public Order() {
    }

    public Order(String id, String cust, String mall, Date createDate, Time createTime, Time lastUpdate, int status, String note,
            double costShopping, double costDelivery, double totalCost, Date dateDelivery, Time timeDelivery, TimeTravel timeTravel, List<OrderDetail> detail) {
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
        this.detail = detail;
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

    public List<OrderDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<OrderDetail> detail) {
        this.detail = detail;
    }
}