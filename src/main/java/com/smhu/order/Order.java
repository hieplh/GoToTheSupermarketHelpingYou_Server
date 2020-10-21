package com.smhu.order;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Objects;

public class Order {

    private String id;
    private String cust;
    private String addressDelivery;
    private String note;
    private String market;
    private String shipper;
    private String lat;
    private String lng;
    private Date createDate;
    private Time createTime;
    private Time lastUpdate;
    private int status;
    private String author;
    private String reasonCancel;
    private double costShopping;
    private double costDelivery;
    private double totalCost;
    private Date dateDelivery;
    private Time timeDelivery;
    private TimeTravel timeTravel;
    private List<OrderDetail> details;
    private List<Evidence> evidences;

    public Order() {
    }

    public Order(String id, String cust, String addressDelivery, String note, String market, 
            String shipper, String lat, String lng, 
            Date createDate, Time createTime, Time lastUpdate, 
            int status, String author, String reasonCancel, double costShopping, double costDelivery, double totalCost, 
            Date dateDelivery, Time timeDelivery, TimeTravel timeTravel, List<OrderDetail> details, List<Evidence> evidences) {
        this.id = id;
        this.cust = cust;
        this.addressDelivery = addressDelivery;
        this.note = note;
        this.market = market;
        this.shipper = shipper;
        this.lat = lat;
        this.lng = lng;
        this.createDate = createDate;
        this.createTime = createTime;
        this.lastUpdate = lastUpdate;
        this.status = status;
        this.author = author;
        this.reasonCancel = reasonCancel;
        this.costShopping = costShopping;
        this.costDelivery = costDelivery;
        this.totalCost = totalCost;
        this.dateDelivery = dateDelivery;
        this.timeDelivery = timeDelivery;
        this.timeTravel = timeTravel;
        this.details = details;
        this.evidences = evidences;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCust() {
        return cust;
    }

    public void setCust(String cust) {
        this.cust = cust;
    }

    public String getAddressDelivery() {
        return addressDelivery;
    }

    public void setAddressDelivery(String addressDelivery) {
        this.addressDelivery = addressDelivery;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
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

    public Time getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Time lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReasonCancel() {
        return reasonCancel;
    }

    public void setReasonCancel(String reasonCancel) {
        this.reasonCancel = reasonCancel;
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

    public Date getDateDelivery() {
        return dateDelivery;
    }

    public void setDateDelivery(Date dateDelivery) {
        this.dateDelivery = dateDelivery;
    }

    public Time getTimeDelivery() {
        return timeDelivery;
    }

    public void setTimeDelivery(Time timeDelivery) {
        this.timeDelivery = timeDelivery;
    }

    public TimeTravel getTimeTravel() {
        return timeTravel;
    }

    public void setTimeTravel(TimeTravel timeTravel) {
        this.timeTravel = timeTravel;
    }

    public List<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }

    public List<Evidence> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<Evidence> evidences) {
        this.evidences = evidences;
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
}
