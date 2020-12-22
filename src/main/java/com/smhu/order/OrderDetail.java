package com.smhu.order;

import com.smhu.food.Food;

public class OrderDetail {

    private String id;
    private Food food;
    private double priceOriginal;
    private double pricePaid;
    private double weight;
    private int saleOff;

    public OrderDetail() {
    }

    public OrderDetail(String id, Food food,
            double priceOrginal, double pricePaid, double weight, int saleOff) {
        this.id = id;
        this.food = food;
        this.priceOriginal = priceOrginal;
        this.pricePaid = pricePaid;
        this.weight = weight;
        this.saleOff = saleOff;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public double getPriceOriginal() {
        return priceOriginal;
    }

    public void setPriceOriginal(double priceOriginal) {
        this.priceOriginal = priceOriginal;
    }

    public double getPricePaid() {
        return pricePaid;
    }

    public void setPricePaid(double pricePaid) {
        this.pricePaid = pricePaid;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getSaleOff() {
        return saleOff;
    }

    public void setSaleOff(int saleOff) {
        this.saleOff = saleOff;
    }

    @Override
    public String toString() {
        return "OrderDetail{" + "id=" + id + ", food=" + food + ", priceOriginal=" + priceOriginal + ", pricePaid=" + pricePaid + ", weight=" + weight + ", saleOff=" + saleOff + '}';
    }

}
