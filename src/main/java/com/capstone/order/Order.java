package com.capstone.order;

import com.capstone.mall.Mall;
import com.google.gson.annotations.SerializedName;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class Order {

    @SerializedName("id")
    private String id;              //server

    private Date createDate;        //server

    private Time createTime;        //server

    private Time lastUpdate;        //server

    private String status;             //server

    private String market;

    @SerializedName("cust")
    private String cust;            //cust

    @SerializedName("mall")
    private Mall mall;            //cust

    @SerializedName("note")
    private String note;            //cust

    @SerializedName("costShipping")
    private double costShopping;    //cust

    @SerializedName("costDelivery")
    private double costDelivery;    //cust

    @SerializedName("totalCost")
    private double totalCost;       //cust

    @SerializedName("dateDelivery")
    private Date dateDelivery;      //cust

    @SerializedName("timeDelivery")
    private Time timeDelivery;      //cust

    private TimeTravel timeTravel;  //cust

    private String shipper;         //shipper

    private Evidence evidence;      //shipper

    @SerializedName("details")
    private List<OrderDetail> details;

    public Order() {
    }

    public Order(String id, String cust, Mall mall, String note, double costShopping, double costDelivery,
            double totalCost, Date dateDelivery, Time timeDelivery, TimeTravel timeTravel) {
        this.id = id;
        this.cust = cust;
        this.mall = mall;
        this.note = note;
        this.costShopping = costShopping;
        this.costDelivery = costDelivery;
        this.totalCost = totalCost;
        this.dateDelivery = dateDelivery;
        this.timeDelivery = timeDelivery;
        this.timeTravel = timeTravel;
    }

    public Order(String id, String cust, String market, Date createDate, Time createTime, Time lastUpdate, String status, String note,
            double costShopping, double costDelivery, double totalCost, Date dateDelivery, Time timeDelivery, TimeTravel timeTravel, List<OrderDetail> detail) {
        this.id = id;
        this.cust = cust;
        this.market = market;
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
        this.details = detail;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getMarket() {
        return market;
    }

    public String getCust() {
        return cust;
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

    public String getShipper() {
        return shipper;
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public List<OrderDetail> getDetails() {
        return details;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Time getCreateTime() {
        return createTime;
    }

    public Time getLastUpdate() {
        return lastUpdate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public void setCust(String cust) {
        this.cust = cust;
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

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", createDate=" + createDate + ", createTime=" + createTime + ", lastUpdate=" + lastUpdate + ", status=" + status + ", market=" + market + ", cust=" + cust + ", mall=" + mall + ", note=" + note + ", costShopping=" + costShopping + ", costDelivery=" + costDelivery + ", totalCost=" + totalCost + ", dateDelivery=" + dateDelivery + ", timeDelivery=" + timeDelivery + ", timeTravel=" + timeTravel + ", shipper=" + shipper + ", evidence=" + evidence + ", details=" + details + '}';
    }

}
