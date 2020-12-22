package com.smhu.food;

import java.util.List;

public class Category {

    private String id;
    private String description;
    private List<Food> foods;

    public Category() {
    }

    public Category(String id, String description, List<Food> foods) {
        this.id = id;
        this.description = description;
        this.foods = foods;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

    @Override
    public String toString() {
        return "Category{" + "id=" + id + ", description=" + description + ", foods=" + foods + '}';
    }

}
