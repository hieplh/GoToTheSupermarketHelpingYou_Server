package com.smhu.response.moderator;

import com.smhu.account.Account;
import com.smhu.account.Shipper;
import com.smhu.market.Market;
import com.smhu.order.Evidence;
import com.smhu.order.Order;
import com.smhu.order.OrderDetail;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class OrderOverall {

    private String id;
    private Account customer;
    private String addressDelivery;
    private String note;
    private Market market;
    private Shipper shipper;
    private String lat;
    private String lng;
    private Date createDate;
    private Time createTime;
    private Time lastUpdate;
    private int status;
    private Account author;
    private String reasonCancel;
    private double costShopping;
    private double costDelivery;
    private double totalCost;
    private double refundCost;
    private Date dateDelivery;
    private Time timeDelivery;
    private List<OrderDetail> details;
    private List<Evidence> evidences;

    public OrderOverall() {
    }

    public OrderOverall(Order order) {
        this.id = order.getId();
        this.addressDelivery = order.getAddressDelivery();
        this.note = order.getNote();
        this.lat = order.getLat();
        this.lng = order.getLng();
        this.createDate = order.getCreateDate();
        this.createTime = order.getCreateTime();
        this.lastUpdate = order.getLastUpdate();
        this.status = order.getStatus();
        this.reasonCancel = order.getReasonCancel();
        this.costShopping = order.getCostShopping();
        this.costDelivery = order.getCostDelivery();
        this.totalCost = order.getTotalCost();
        this.refundCost = order.getRefundCost();
        this.dateDelivery = order.getDateDelivery();
        this.timeDelivery = order.getTimeDelivery();
        this.details = order.getDetails();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Account getCustomer() {
        return customer;
    }

    public void setCustomer(Account customer) {
        this.customer = customer;
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

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public Shipper getShipper() {
        return shipper;
    }

    public void setShipper(Shipper shipper) {
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

    public Account getAuthor() {
        return author;
    }

    public void setAuthor(Account author) {
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

    public double getRefundCost() {
        return refundCost;
    }

    public void setRefundCost(double refundCost) {
        this.refundCost = refundCost;
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
    public String toString() {
        return "OrderOverall{" + "id=" + id + ", customer=" + customer + ", addressDelivery=" + addressDelivery + ", note=" + note + ", market=" + market + ", shipper=" + shipper + ", lat=" + lat + ", lng=" + lng + ", createDate=" + createDate + ", createTime=" + createTime + ", lastUpdate=" + lastUpdate + ", status=" + status + ", author=" + author + ", reasonCancel=" + reasonCancel + ", costShopping=" + costShopping + ", costDelivery=" + costDelivery + ", totalCost=" + totalCost + ", refundCost=" + refundCost + ", dateDelivery=" + dateDelivery + ", timeDelivery=" + timeDelivery + ", details=" + details + ", evidences=" + evidences + '}';
    }
}
