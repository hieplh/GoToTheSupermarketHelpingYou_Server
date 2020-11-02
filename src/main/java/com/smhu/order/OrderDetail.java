package com.smhu.order;

public class OrderDetail {

    private String id;
    private String foodId;
    private String foodName;
    private String image;
    private double priceOriginal;
    private double pricePaid;
    private double weight;
    private int saleOff;

    public OrderDetail() {
    }

    public OrderDetail(String id, String food, String foodName, String image,
            double priceOrginal, double pricePaid, double weight, int saleOff) {
        this.id = id;
        this.foodId = food;
        this.image = image;
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

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

}
