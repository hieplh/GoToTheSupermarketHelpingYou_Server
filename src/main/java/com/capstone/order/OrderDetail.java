/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.order;

/**
 *
 * @author Admin
 */
public class OrderDetail {

    private String id;
    private String food;
    private double priceOrginal;
    private double pricePaid;
    private double weight;
    private int saleOff;

    public OrderDetail() {
    }

    public OrderDetail(String id, String food, double priceOrginal, double pricePaid, double weight, int saleOff) {
        this.id = id;
        this.food = food;
        this.priceOrginal = priceOrginal;
        this.pricePaid = pricePaid;
        this.weight = weight;
        this.saleOff = saleOff;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public double getPriceOrginal() {
        return priceOrginal;
    }

    public void setPriceOrginal(double priceOrginal) {
        this.priceOrginal = priceOrginal;
    }

    public double getPricePaid() {
        return pricePaid;
    }

    public void setPricePaid(double pricePaid) {
        this.pricePaid = pricePaid;
    }

    public int getSaleOff() {
        return saleOff;
    }

    public void setSaleOff(int saleOff) {
        this.saleOff = saleOff;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
