package com.smhu.response.shipper;

import com.smhu.controller.MarketController;
import com.smhu.entity.Market;
import com.smhu.order.Order;
import com.smhu.order.OrderDetail;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class OrderDelivery {

//    @SerializedName("destination")
//    private String destination; // address delivery
//
//    @SerializedName("value")
//    private int value; // int 14300
//    @SerializedName("order")
//    private OrderShipper order;
    private String id;
    private String cust;
    private String addressDelivery;
    private Market market;
    private String note;
    private String shipper;
    private int status;
    private double costShopping;
    private double costDelivery;
    private double totalCost;
    private Date dateDelivery;
    private Time timeDelivery;
    private List<OrderDetail> details;

    public OrderDelivery() {
    }

    public OrderDelivery(Order order) {
        this.id = order.getId();
        this.cust = order.getCust();
        this.addressDelivery = order.getAddressDelivery();
        this.market = MarketController.mapMarket.get(order.getMarket());
        this.note = order.getNote();
        this.shipper = order.getShipper();
        this.status = order.getStatus();
        this.costShopping = order.getCostShopping();
        this.costDelivery = order.getCostDelivery();
        this.totalCost = order.getTotalCost();
        this.dateDelivery = order.getDateDelivery();
        this.timeDelivery = order.getTimeDelivery();
        this.details = order.getDetails();
    }

    public String getId() {
        return id;
    }

    public String getCust() {
        return cust;
    }

    public String getAddressDelivery() {
        return addressDelivery;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setCust(String cust) {
        this.cust = cust;
    }

    public void setAddressDelivery(String addressDelivery) {
        this.addressDelivery = addressDelivery;
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

    @Override
    public String toString() {
        return "OrderShipper{" + "id=" + id + ", cust=" + cust + ", addressDelivery=" + addressDelivery + ", market=" + market + ", note=" + note + ", shipper=" + shipper + ", status=" + status + ", costShopping=" + costShopping + ", costDelivery=" + costDelivery + ", totalCost=" + totalCost + ", dateDelivery=" + dateDelivery + ", timeDelivery=" + timeDelivery + ", details=" + details + '}';
    }
}
