package com.smhu.food;

public class Food {

    private String id;
    private String name;
    private String image;
    private String description;
    private double price;
    private SaleOff saleOff;

    public Food() {
    }

    public Food(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public Food(String id, String name, String image, String description, double price) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        this.price = price;
    }

    public Food(String id, String name, String image, String description, double price, SaleOff saleOff) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        this.price = price;
        this.saleOff = saleOff;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public SaleOff getSaleOff() {
        return saleOff;
    }

    public void setSaleOff(SaleOff saleOff) {
        this.saleOff = saleOff;
    }

    @Override
    public String toString() {
        return "Food{" + "id=" + id + ", name=" + name + ", image=" + image + ", description=" + description + ", price=" + price + ", saleOff=" + saleOff + '}';
    }
}
