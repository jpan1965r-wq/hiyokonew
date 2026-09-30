package com.example.model;

import java.io.Serializable;

public class FoodCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer foodCategoryId;
    private String foodCategoryName;

    public FoodCategory() {
    }

    public FoodCategory(Integer foodCategoryId, String foodCategoryName) {
        this.foodCategoryId = foodCategoryId;
        this.foodCategoryName = foodCategoryName;
    }

    public Integer getFoodCategoryId() {
        return foodCategoryId;
    }

    public void setFoodCategoryId(Integer foodCategoryId) {
        this.foodCategoryId = foodCategoryId;
    }

    public String getFoodCategoryName() {
        return foodCategoryName;
    }

    public void setFoodCategoryName(String foodCategoryName) {
        this.foodCategoryName = foodCategoryName;
    }
}
