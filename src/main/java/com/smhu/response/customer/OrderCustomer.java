package com.smhu.response.customer;

import com.smhu.order.OrderDetail;
import com.smhu.order.TimeTravel;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class OrderCustomer {

    private String cust;
    private String addressDelivery;
    private String market;
    private String note;
    private double costShopping;
    private double costDelivery;
    private double totalCost;
    private Date dateDelivery;
    private Time timeDelivery;
    private TimeTravel timeTravel;
    private List<OrderDetail> details;

    public OrderCustomer() {
    }

    public OrderCustomer(String cust, String addressDelivery, String market, String note, double costShopping, double costDelivery, double totalCost, Date dateDelivery, Time timeDelivery, TimeTravel timeTravel, List<OrderDetail> details) {
        this.cust = cust;
        this.addressDelivery = addressDelivery;
        this.market = market;
        this.note = note;
        this.costShopping = costShopping;
        this.costDelivery = costDelivery;
        this.totalCost = totalCost;
        this.dateDelivery = dateDelivery;
        this.timeDelivery = timeDelivery;
        this.timeTravel = timeTravel;
        this.details = details;
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

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
}
