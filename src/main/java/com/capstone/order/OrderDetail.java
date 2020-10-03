package com.capstone.order;

import com.google.gson.annotations.SerializedName;

public class OrderDetail {

    @SerializedName("id")
    private String id;

    @SerializedName("food")
    private String food;

    @SerializedName("image")
    private String image;

    @SerializedName("priceOriginal")
    private double priceOriginal;

    @SerializedName("pricePaid")
    private double pricePaid;

    @SerializedName("weight")
    private double weight;

    @SerializedName("saleOff")
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

    public void setImage(String image) {
        this.image = image;
    }

}
