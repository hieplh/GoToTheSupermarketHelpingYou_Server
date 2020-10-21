package com.smhu.order;

import com.google.gson.annotations.SerializedName;

public class OrderDetail {

    private String id;
    private String food;
    private String image;
    private double priceOriginal;
    private double pricePaid;
    private double weight;
    private int saleOff;

    public OrderDetail() {
    }

    public OrderDetail(String id, String food, String image, double priceOrginal, double pricePaid, double weight, int saleOff) {
        this.id = id;
        this.food = food;
        this.image = image;
        this.priceOriginal = priceOrginal;
        this.pricePaid = pricePaid;
        this.weight = weight;
        this.saleOff = saleOff;
    }

    public String getId() {
        return id;
    }

    public String getFood() {
        return food;
    }

    public double getPriceOriginal() {
        return priceOriginal;
    }

    public double getPricePaid() {
        return pricePaid;
    }

    public double getWeight() {
        return weight;
    }

    public int getSaleOff() {
        return saleOff;
    }

    public String getImage() {
        return image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public void setPriceOriginal(double priceOriginal) {
        this.priceOriginal = priceOriginal;
    }

    public void setPricePaid(double pricePaid) {
        this.pricePaid = pricePaid;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setSaleOff(int saleOff) {
        this.saleOff = saleOff;
    }
}
