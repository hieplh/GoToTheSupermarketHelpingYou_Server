package com.smhu.order;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Objects;

public class Order {

    private String id;              //server

    private Date createDate;        //server

    private Time createTime;        //server

    private Time lastUpdate;        //server

    private String status;             //server

    private String market;

    private String cust;            //cust

//    @SerializedName("mall")
//    private Mall mall;            //cust
    private String note;            //cust

    private double costShopping;    //cust

    private double costDelivery;    //cust

    private double totalCost;       //cust

    private Date dateDelivery;      //cust

    private Time timeDelivery;      //cust

    private TimeTravel timeTravel;  //cust

    private String shipper;         //shipper

    private Evidence evidence;      //shipper

    private List<OrderDetail> details;

    private String lat;

    private String lng;

    public Order() {
    }

    public Order(String id, String cust, String market, Date createDate, Time createTime, Time lastUpdate, String status, String note,
            double costShopping, double costDelivery, double totalCost, Date dateDelivery, Time timeDelivery, TimeTravel timeTravel,
            String lat, String lng, List<OrderDetail> detail) {
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
        this.lat = lat;
        this.lng = lng;
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

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
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

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Order other = (Order) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", createDate=" + createDate + ", createTime=" + createTime + ", lastUpdate=" + lastUpdate + ", status=" + status + ", market=" + market + ", cust=" + cust + ", note=" + note + ", costShopping=" + costShopping + ", costDelivery=" + costDelivery + ", totalCost=" + totalCost + ", dateDelivery=" + dateDelivery + ", timeDelivery=" + timeDelivery + ", timeTravel=" + timeTravel + ", shipper=" + shipper + ", evidence=" + evidence + ", details=" + details + ", lat=" + lat + ", lng=" + lng + '}';
    }

}
