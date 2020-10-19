package com.smhu.response;

import com.google.gson.annotations.SerializedName;
import com.smhu.market.Market;
import com.smhu.order.OrderDetail;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class OrderObj {

    @SerializedName("id")
    private String id;              //server

    @SerializedName("cust")
    private String cust;            //cust

    @SerializedName("market")
    private Market market;            //cust

    @SerializedName("note")
    private String note;            //cust

    @SerializedName("shipper")
    private String shipper;

    @SerializedName("status")
    private int status;

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

    @SerializedName("details")
    private List<OrderDetail> details;

    @SerializedName("lat")
    private String lat;

    @SerializedName("lng")
    private String lng;

    public OrderObj() {
    }

    public OrderObj(String id, String cust, Market market, String note, String shipper, int status,
            double costShopping, double costDelivery, double totalCost,
            Date dateDelivery, Time timeDelivery, List<OrderDetail> details, String lat, String lng) {
        this.id = id;
        this.cust = cust;
        this.market = market;
        this.note = note;
        this.shipper = shipper;
        this.status = status;
        this.costShopping = costShopping;
        this.costDelivery = costDelivery;
        this.totalCost = totalCost;
        this.dateDelivery = dateDelivery;
        this.timeDelivery = timeDelivery;
        this.details = details;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public String getCust() {
        return cust;
    }

    public Market getMarket() {
        return market;
    }

    public String getNote() {
        return note;
    }

    public String getShipper() {
        return shipper;
    }

    public int getStatus() {
        return status;
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

    public List<OrderDetail> getDetails() {
        return details;
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

    public void setCust(String cust) {
        this.cust = cust;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
