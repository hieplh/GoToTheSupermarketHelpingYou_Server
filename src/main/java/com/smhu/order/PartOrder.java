package com.smhu.order;

import java.sql.Date;

public class PartOrder {

    private Date createDate;
    private double costDelivery;
    private double costShopping;
    private int commissionShipping;
    private int commissionShopping;

    public PartOrder() {
    }

    public PartOrder(Date createDate, double costDelivery, double costShopping, int commissionShipping, int commissionShopping) {
        this.createDate = createDate;
        this.costDelivery = costDelivery;
        this.costShopping = costShopping;
        this.commissionShipping = commissionShipping;
        this.commissionShopping = commissionShopping;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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

    public int getCommissionShipping() {
        return commissionShipping;
    }

    public void setCommissionShipping(int commissionShipping) {
        this.commissionShipping = commissionShipping;
    }

    public int getCommissionShopping() {
        return commissionShopping;
    }

    public void setCommissionShopping(int commissionShopping) {
        this.commissionShopping = commissionShopping;
    }

    @Override
    public String toString() {
        return "Order{" + "createDate=" + createDate + ", costShopping=" + costShopping + ", costDelivery=" + costDelivery + ", commissionShipping=" + commissionShipping + ", commissionShopping=" + commissionShopping + '}';
    }

}
